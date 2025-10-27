package Project1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WatchDirectory implements AutoCloseable, Runnable {
    private final Path inputDir;
    private final Path acceptedDir;
    private final Path rejectedDir;
    private final ServiceFacade service;

    private final WatchService watchService;
    private volatile boolean running = false;
    private Thread loopThread;

    // Debounce: file path -> last seen nano time
    private final Map<Path, Long> inFlight = new ConcurrentHashMap<>();

    // Tuning knobs
    private final Duration maxWait = Duration.ofSeconds(6);
    private final Duration poll    = Duration.ofMillis(150);
    private final boolean processOnModify = true;

    public WatchDirectory(Path inputDir, Path acceptedDir, Path rejectedDir, ServiceFacade service) throws Exception {
        this.inputDir    = inputDir;
        this.acceptedDir = acceptedDir;
        this.rejectedDir = rejectedDir;
        this.service     = service;

        Files.createDirectories(this.inputDir);
        Files.createDirectories(this.acceptedDir);
        Files.createDirectories(this.rejectedDir);

        this.watchService = FileSystems.getDefault().newWatchService();
        this.inputDir.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        loopThread = new Thread(this, "order-watcher");
        loopThread.setDaemon(true);
        loopThread.start();
    }

    @Override
    public void run() {
        try (WatchService ws = FileSystems.getDefault().newWatchService()) {
            inputDir.register(ws,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            for (;;) {
                WatchKey key = ws.take();

                // collect paths seen in this batch
                java.util.Set<Path> batch = new java.util.HashSet<>();
                for (WatchEvent<?> ev : key.pollEvents()) {
                    Object ctx = ev.context();
                    if (ctx instanceof Path p) {
                        Path file = inputDir.resolve(p);
                        batch.add(file);
                    }
                }
                key.reset();

                // debounce a touch so multiple MODIFYs collapse
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}

                for (Path file : batch) {
                    try {
                        String name = file.getFileName().toString();
                        String lower = name.toLowerCase();

                        // skip non-regular and sidecar files
                        if (!Files.isRegularFile(file)) continue;
                        if (name.startsWith(".")) continue;
                        if (lower.endsWith(".reason.txt")) continue;

                        // unsupported extension -> reject
                        if (!lower.endsWith(".json") && !lower.endsWith(".xml")) {
                            moveToRejected(file, "Unsupported extension");
                            continue;
                        }

                        // wait for stability (file finished copying)
                        boolean stable = waitUntilStable(file, 5000, 200);

                            if (!waitUntilStable(file, maxWait.toMillis(), poll.toMillis())) {
                                // Skip this round; the file stays in Input Orders and will be retried
                                continue;
                            }


                        // quick guard: zero-length files still happen sometimes
                        if (Files.size(file) == 0) {
                            moveToRejected(file, "Empty file (0 bytes)");
                            continue;
                        }

                        // try parsing; allow a few lightweight retries in case AV/indexer touches file
                        OrderWrapper ow = null;
                        for (int i = 0; i < 3 && ow == null; i++) {
                            ow = safeParse(file);
                            if (ow == null) Thread.sleep(150);
                        }

                        if (ow != null && ow.getOrder() != null) {
                            service.upsertReplacingById(ow); // your existing method
                            moveTo(acceptedDir.resolve(file.getFileName()), file);
                            service.notifyChangePersist();              // keep your existing persist/notify
                        } else {
                            moveToRejected(file, "Parse failed");
                        }
                    } catch (Exception e) {
                        try { moveToRejected(file, "Watcher exception: " + e.getMessage()); } catch (Exception ignore) {}
                    }
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // log if you want
        }
    }


    private void processOne(Path file) {
        try {
            // Wait until file is stable (don’t touch if not ready yet)
            if (!waitUntilStable(file, maxWait.toMillis(), poll.toMillis())) return;

            String name = file.getFileName().toString().toLowerCase();
            boolean isJson = name.endsWith(".json");
            boolean isXml  = name.endsWith(".xml");
            if (!isJson && !isXml) { moveToRejected(file, "Unsupported extension"); return; }

            // Retry parse a few times in case the writer just finished closing
            final int maxAttempts = 3;
            final long backoffMs  = 250;
            OrderWrapper ow = null;
            Exception last = null;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    ow = isJson ? Serialization.parseJson(file) : Serialization.parseXml(file);
                    if (ow != null && ow.getOrder() != null) break; // success
                    last = new IllegalStateException("Parsed wrapper or order was null");
                } catch (Exception ex) {
                    last = ex;
                }
                if (attempt < maxAttempts) {
                    try { Thread.sleep(backoffMs); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); break;
                    }
                }
            }

            if (ow == null || ow.getOrder() == null) {
                moveToRejected(file, "Parse failed after retries: " + (last == null ? "unknown" : last.getMessage()));
                return;
            }

            // Success → add to system and move to Accepted
            service.addParsedOrder(ow);
            moveTo(acceptedDir.resolve(file.getFileName()), file);

        } catch (Exception ex) {
            try { moveToRejected(file, ex.getClass().getSimpleName() + ": " + ex.getMessage()); }
            catch (Exception ignore) {}
        } finally {
            inFlight.remove(file);
        }
    }


    // ---------------- File moves for refresh ----------------
    void moveTo(Path target, Path source) throws IOException, IOException {
        Files.createDirectories(target.getParent());
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
    void moveToRejected(Path source, String reason) {
        try {
            Path target = rejectedDir.resolve(source.getFileName());
            moveTo(target, source);
            try {
                Path why = target.resolveSibling(target.getFileName().toString() + ".reason.txt");
                Files.writeString(why, reason == null ? "Unknown" : reason);
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}
    }

    private static boolean isCandidate(Path p) {
        try {
            String n = p.getFileName().toString();
            if (n.startsWith(".") || n.startsWith("~")) return false;

            String lower = n.toLowerCase();
            // Ignore common partial/temporary download names
            if (lower.endsWith(".tmp") || lower.endsWith(".part") || lower.endsWith(".crdownload")) return false;

            // Only accept real json/xml files
            return lower.endsWith(".json") || lower.endsWith(".xml");
        } catch (Exception e) {
            return false;
        }
    }


    private boolean waitUntilStable(Path file, long timeoutMs, long pollMs) {
        long end = System.currentTimeMillis() + timeoutMs;
        long last = -1L, same = 0;
        try {
            while (System.currentTimeMillis() < end) {
                if (!Files.exists(file) || !Files.isRegularFile(file)) return false;
                long size = Files.size(file);
                if (size == last) {
                    if (++same >= 2) return size > 0; // must be > 0
                } else {
                    same = 0;
                }
                last = size;
                Thread.sleep(pollMs);
            }
        } catch (Exception ignored) {}
        return false;
    }


    @Override public synchronized void close() throws Exception {
        running = false;
        if (loopThread != null) loopThread.interrupt();
        try { watchService.close(); } catch (Exception ignore) {}
    }

    private OrderWrapper safeParse(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".json")) return Serialization.parseJson(file);
        if (name.endsWith(".xml"))  return Serialization.parseXml(file);
        return null; // unsupported extension handled by caller
    }
}
