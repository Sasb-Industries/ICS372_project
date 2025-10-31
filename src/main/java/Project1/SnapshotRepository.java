package Project1;

import java.io.IOException;
import java.util.Map;

public interface SnapshotRepository {
    Map<Integer,OrderWrapper> load() throws IOException;
    void save(Map<Integer,OrderWrapper> state) throws IOException;
}
