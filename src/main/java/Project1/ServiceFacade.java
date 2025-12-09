package Project1;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public final class ServiceFacade implements AutoCloseable {
    // Directories
    private final Path inputDir;             // e.g. "Input Orders"
    private final Path acceptedDir;          // e.g. "Accepted Orders"
    private final Path rejectedDir;          // e.g. "Rejected"
    private final Path runningListDir;       // e.g. "Running List"
    private final Path snapshotFile;         // Running List/orders.json
    // Domain + persistence
    private final Restaurant restaurant;
    private final SnapshotRepository repo;

    // UI listeners
    private final List<OrdersChangedListener> listeners = new ArrayList<>();

    // Watcher
    private WatchDirectory watcher;

    public ServiceFacade(Path inputDir) throws Exception {
        this(inputDir,
                inputDir.resolveSibling("Accepted Orders"),
                inputDir.resolveSibling("Rejected"),
                Paths.get("Running List"),
                "orders.json");
    }

    public ServiceFacade(Path inputDir, Path acceptedDir, Path rejectedDir,
                         Path runningListDir, String snapshotName) throws Exception {
        this.inputDir       = inputDir;
        this.acceptedDir    = acceptedDir;
        this.rejectedDir    = rejectedDir;
        this.runningListDir = runningListDir;
        this.snapshotFile   = runningListDir.resolve(snapshotName);

        // Ensure folders exist
        Files.createDirectories(this.inputDir);
        Files.createDirectories(this.acceptedDir);
        Files.createDirectories(this.rejectedDir);
        Files.createDirectories(this.runningListDir);

        // Repo
        this.repo = new FileSnapshotRepository(this.snapshotFile);

        // Resume from snapshot
        Map<Integer,OrderWrapper> saved = Optional.ofNullable(repo.load()).orElse(Map.of());
        int maxIdSeen = saved.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        this.restaurant = new Restaurant(saved, maxIdSeen);

        this.watcher = new WatchDirectory(this.inputDir, this.acceptedDir, this.rejectedDir, this);

        // Bootstrap if empty
        if (saved.isEmpty()) {
            refreshFromDisk(); // also saves + notifies
        } else {
            notifyChangePersist(); // save-as-is & notify
        }

        // Start the watcher
        this.watcher.start();
    }

    // ---------------- Public API (read) ----------------
    public Map<Integer, OrderWrapper> listAll() { return restaurant.all(); }
    public OrderWrapper get(int id) { return restaurant.get(id); }
    public List<OrderWrapper> listIncomplete() { return restaurant.incomplete(); }
    public double totalFor(int id) { return restaurant.totalFor(id); }

    // ---------------- Public API (commands) ------------
    public void startOrder(int id) { restaurant.start(id); notifyChangePersist(); }
    public void completeOrder(int id) { restaurant.complete(id); notifyChangePersist(); }
    public boolean deleteOrderIfNew(int id) {
        boolean ok = restaurant.deleteIfNew(id);
        if (ok) notifyChangePersist();
        return ok;
    }
    public void upsertReplacingById(OrderWrapper ow) {
        restaurant.upsertReplacingById(ow);   // call the domain method you already have
        notifyChangePersist();                // persist + listeners as you already do
    }

    /** Called by the watcher (or by any parser) when exactly one file yields a parsed order. */
    public void addParsedOrder(OrderWrapper wrapper) {
        if (wrapper == null || wrapper.getOrder() == null) return;
        restaurant.add(wrapper); // assigns id if missing; keeps generator ahead
        notifyChangePersist();
    }

    public synchronized void refreshFromDisk() throws IOException {
        try (Stream<Path> files = Files.list(inputDir)) {
            files.filter(Files::isRegularFile).forEach(p -> {
                try {
                    String name = p.getFileName().toString().toLowerCase();
                    OrderWrapper ow = null;

                    if (name.endsWith(".json"))      ow = Serialization.parseJson(p);
                    else if (name.endsWith(".xml"))  ow = Serialization.parseXml(p);
                    else {                           // unsupported extension
                        watcher.moveToRejected(p, "Unsupported extension");
                        return;
                    }

                    if (ow != null && ow.getOrder() != null) {
                        // merge/replace if id present, assign if missing (Restaurant handles it)
                        restaurant.upsertReplacingById(ow);
                        // mirror watcher: archive successful file so refresh won’t reparse it again
                        watcher.moveTo(acceptedDir.resolve(p.getFileName()), p);
                    } else {
                        // parsing failed here -> reject it
                        watcher.moveToRejected(p, "Parse failed during refresh");
                    }
                } catch (Exception e) {
                    System.err.println("Refresh failed for " + p + ": " + e.getMessage());
                    try { watcher.moveToRejected(p, "Refresh exception: " + e.getMessage()); } catch (Exception ignore) {}
                }
            });
        }
        notifyChangePersist();
    }


    // ---------------- Listeners ----------------
    public void addListener(OrdersChangedListener l) { if (l != null) listeners.add(l); }
    private void notifyListeners() { for (var l : listeners) l.onOrdersChanged(); }

    // ---------------- Persistence+Notify ---------------
    void notifyChangePersist() {
        try { repo.save(restaurant.all()); }
        catch (IOException e) { System.err.println("Snapshot save failed: " + e.getMessage()); }
        notifyListeners();
    }


    // ---------------- Printing ----------------
    /** Write all orders to a JSON file via RestaurantPrinter/Serialization. */
    public void writeAllOrders(File outFile) {
        try {
            new RestaurantPrinter(restaurant).writeAllOrdersToFile(outFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write orders to file: " + e.getMessage(), e);
        }
    }
    // ---------------- Lifecycle ----------------
    @Override public void close() {
        try { if (watcher != null) watcher.close(); } catch (Exception ignore) {}
    }
}
