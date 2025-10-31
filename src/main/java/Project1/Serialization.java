package Project1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

public final class Serialization {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final XmlMapper XML = new XmlMapper();

    private Serialization() {
    }


    /* ---------- Parse a single on-disk order file ---------- */

    // Serialization.java
    /** Parse one JSON file -> OrderWrapper. On error, just log and return null. */
    public static OrderWrapper parseJson(Path file) {
        try {
            OrderWrapper w = JSON.readValue(file.toFile(), OrderWrapper.class);
            if (w != null && w.getOrder() != null) {
                w.getOrder().setSource("Foodhub");      // tag source by extension
            }
            return w;
        } catch (Exception e) {
            System.err.println("JSON parse failed: " + e.getMessage());
            return null;
        }
    }

    /** Parse one XML file -> OrderWrapper. On error, just log and return null. */
    public static OrderWrapper parseXml(Path file) {
        try {
            OrderWrapper w = XML.readValue(file.toFile(), OrderWrapper.class);
            if (w != null && w.getOrder() != null) {
                w.getOrder().setSource("KingEats");     // tag source by extension
            }
            return w;
        } catch (Exception e) {
            System.err.println("XML parse failed: " + e.getMessage());
            return null;
        }
    }

    /* ---------- Snapshot read / write (Running List/orders.json) ---------- */

    /** Read the snapshot file -> Map<Integer, OrderWrapper>. Returns null if missing or invalid. */
    public static Map<Integer, OrderWrapper> readSnapshot(Path snapshotFile) {
        try {
            if (!Files.exists(snapshotFile)) return null;
            return JSON.readValue(snapshotFile.toFile(),
                    new TypeReference<Map<Integer, OrderWrapper>>() {});
        } catch (Exception e) {
            System.err.println("Snapshot read failed: " + e.getMessage());
            return null;
        }
    }


    // Takes our hashmap and writes the output to a JSON file
    public static void writeOrder(Map<Integer, OrderWrapper> orderMap, File outFile) throws IOException {
        JSON.writeValue(outFile, orderMap);

    }
}
