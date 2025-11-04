package Project1;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Map;

public final class FileSnapshotRepository implements SnapshotRepository {
    private final Path snapshotFile;
    public FileSnapshotRepository(Path snapshotFile) {
        this.snapshotFile = snapshotFile; }

    @Override public Map<Integer, OrderWrapper> load() throws IOException {
        return Serialization.readSnapshot(snapshotFile); // your existing helper
    }
    @Override public void save(Map<Integer,OrderWrapper> state) throws IOException {
        Serialization.writeOrder(state, snapshotFile.toFile());   // your existing helper
    }
}
