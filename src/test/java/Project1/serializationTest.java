package Project1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SerializationTest {

    @TempDir
    Path tmp;

    // ----- Making temporary usable order map to test -----
    private Map<Integer, OrderWrapper> sampleOrders() {
        Map<Integer, OrderWrapper> m = new LinkedHashMap<>();

        // Minimal Order with no fields set is fine for now
        // (Jackson will still serialize/deserialize it.)
        Order o1 = new Order();
        OrderWrapper w1 = new OrderWrapper();
        w1.setOrder(o1);
        m.put(7, w1);

        // Add a second distinct entry so we verify map keys stay
        Order o2 = new Order();
        OrderWrapper w2 = new OrderWrapper();
        w2.setOrder(o2);
        m.put(42, w2);

        return m;
    }

    // Starting with sample orders we'll write then reload then test
    // Testing the whole system
    @Test
    void writeThenRead() throws IOException {
        // Write
        Map<Integer, OrderWrapper> original = sampleOrders();  // fake order
        File out = tmp.resolve("orders.json").toFile();

        Serialization.writeOrder(original, out);
        Map<Integer, OrderWrapper> reloaded = Serialization.readSnapshot(out.toPath());

        assertNotNull(reloaded, "readSnapshot should return a map after writing");
        assertEquals(original.keySet(), reloaded.keySet(), "Map keys should round-trip intact");
        // Check specific values that should NOT be null
        assertNotNull(reloaded.get(7), "Wrapper for key 7 should exist");
        assertNotNull(reloaded.get(7).getOrder(), "Order for key 7 should not be null");
        assertNotNull(reloaded.get(42), "Wrapper for key 42 should exist");
        assertNotNull(reloaded.get(42).getOrder(), "Order for key 42 should not be null");

        // Inverse of checks above
        //assertNull(reloaded.get(40), "Wrapper for key 40 should NOT exist");
        //assertNull(reloaded.get(40).getOrder(), "Order for key 42 should be null");
    }

    // readSnapshot should return an empty map if the json obj is empty itself
    @Test
    void readSnapshot_empty() throws IOException {
        Path snap = tmp.resolve("empty.json");
        Files.writeString(snap, "{}", StandardCharsets.UTF_8);

        Map<Integer, OrderWrapper> reloaded = Serialization.readSnapshot(snap);

        assertNotNull(reloaded, "Empty JSON object should deserialize to an empty map, not null");
        assertTrue(reloaded.isEmpty(), "Expected empty map for {}");
    }

    // Testing with bad JSON - should return null in case of bad json
    @Test
    void readSnapshot_badJson() throws IOException {
        Path snap = tmp.resolve("bad.json");
        Files.writeString(snap, "{ not: valid json", StandardCharsets.UTF_8);

        Map<Integer, OrderWrapper> reloaded = Serialization.readSnapshot(snap);

        assertNull(reloaded, "On malformed input, readSnapshot should return null (and log an error)");
    }

    @Test
    void writeOrder() throws IOException {
        Map<Integer, OrderWrapper> original = sampleOrders();
        Path outPath = tmp.resolve("orders.json");
        File out = outPath.toFile();

        Serialization.writeOrder(original, out);

        assertTrue(out.exists(), "Output file should be created");
        String text = Files.readString(outPath);
        assertFalse(text.isBlank(), "Output file should contain JSON");
    }
}
