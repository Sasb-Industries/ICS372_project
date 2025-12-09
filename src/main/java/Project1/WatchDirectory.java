package Project1;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class WatchDirectory implements AutoCloseable, Runnable {
    private final Path inputDir;
    private final Path acceptedDir;
    private final Path rejectedDir;
    private final ServiceFacade service;

    private final WatchService watchService;
    private volatile boolean running = false;
    private Thread loopThread;

    // Debounce map (kept for future use if you want it; remove if not needed)
    private final Map<Path, Long> inFlight = new ConcurrentHashMap<>();

    // Tuning knobs
    private final Duration maxWait = Duration.ofSeconds(6);
    private final Duration poll    = Duration.ofMillis(150);

    public WatchDirectory(Path inputDir, Path acceptedDir, Path rejectedDir, ServiceFacade service) throws IOException {
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
                ENTRY_CREATE,
                ENTRY_MODIFY
        );
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
        try {
            while (running) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }

                Set<Path> batch = new HashSet<>();
                for (WatchEvent<?> ev : key.pollEvents()) {
                    Object ctx = ev.context();
                    if (ctx instanceof Path p) {
                        Path file = inputDir.resolve(p);
                        batch.add(file);
                    }
                }
                key.reset();

                // small debounce
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }

                for (Path file : batch) {
                    processOne(file);
                }
            }
        } catch (Exception e) {
            // log if you want
        }
    }

    private void processOne(Path file) {
        try {
            // Wait until file is stable (size not changing, > 0)
            if (!waitUntilStable(file, maxWait.toMillis(), poll.toMillis())) return;

            String name  = file.getFileName().toString();
            String lower = name.toLowerCase();

            if (!Files.isRegularFile(file) ||
                    name.startsWith(".") ||
                    lower.endsWith(".reason.txt")) {
                return;
            }

            boolean isJson = lower.endsWith(".json");
            boolean isXml  = lower.endsWith(".xml");
            if (!isJson && !isXml) {
                moveToRejected(file, "Unsupported extension");
                return;
            }

            // Parse with a few retries
            final int maxAttempts = 3;
            final long backoffMs  = 250;
            OrderWrapper ow = null;
            Exception last = null;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    ow = isJson ? Serialization.parseJson(file)
                            : Serialization.parseXml(file);
                    if (ow != null && ow.getOrder() != null) break;
                    last = new IllegalStateException("Parsed wrapper or order was null");
                } catch (Exception ex) {
                    last = ex;
                }
                if (attempt < maxAttempts) {
                    try { Thread.sleep(backoffMs); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            if (ow == null || ow.getOrder() == null) {
                moveToRejected(file, "Parse failed after retries: " +
                        (last == null ? "unknown" : last.getMessage()));
                return;
            }

            // Success → tell service and move to Accepted
            service.addParsedOrder(ow);
            moveTo(acceptedDir.resolve(file.getFileName()), file);

        } catch (Exception ex) {
            try {
                moveToRejected(file, ex.getClass().getSimpleName() + ": " + ex.getMessage());
            } catch (Exception ignore) {
            }
        } finally {
            inFlight.remove(file);
        }
    }

    void moveTo(Path target, Path source) throws IOException {
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

    private boolean waitUntilStable(Path file, long timeoutMs, long pollMs) {
        long end  = System.currentTimeMillis() + timeoutMs;
        long last = -1L;
        int same  = 0;
        try {
            while (System.currentTimeMillis() < end) {
                if (!Files.exists(file) || !Files.isRegularFile(file)) return false;
                long size = Files.size(file);
                if (size == last) {
                    if (++same >= 2) return size > 0;
                } else {
                    same = 0;
                }
                last = size;
                Thread.sleep(pollMs);
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    public synchronized void close() throws Exception {
        running = false;
        if (loopThread != null) loopThread.interrupt();
        try {
            watchService.close();
        } catch (Exception ignore) {}
    }
}
