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
            return JSON.readValue(Files.readAllBytes(file), OrderWrapper.class);
        } catch (Exception e) {
            System.err.println("Bad JSON: " + file + " -> " + e.getMessage());
            return null;
        }
    }

    /** Parse one XML file -> OrderWrapper. On error, just log and return null. */
    public static OrderWrapper parseXml(Path file) {
        try {
            return XML.readValue(Files.readAllBytes(file), OrderWrapper.class);
        } catch (Exception e) {
            System.err.println("Bad XML: " + file + " -> " + e.getMessage());
            return null;
        }
    }


    /** Move a bad file into ./Rejected with a .bad suffix. */
    private static void quarantine(Path file) {
        try {
            Path rejected = file.getParent().resolveSibling("Rejected");
            Files.createDirectories(rejected);
            String name = file.getFileName().toString() + "." + System.currentTimeMillis() + ".bad";
            Files.move(file, rejected.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            System.err.println("Quarantined: " + file + " -> " + rejected.resolve(name));
        } catch (IOException ex) {
            System.err.println("Quarantine failed for " + file + ": " + ex.getMessage());
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

    /** Write the current cache to snapshot JSON. (Pretty basic for class use.) */
    public static void writeSnapshot(Map<Integer, OrderWrapper> orderMap, Path snapshotFile) {
        try {
            Files.createDirectories(snapshotFile.getParent());
            JSON.writerWithDefaultPrettyPrinter().writeValue(snapshotFile.toFile(), orderMap);
        } catch (Exception e) {
            System.err.println("Snapshot write failed: " + e.getMessage());
        }
    }


    // Takes our hashmap and writes the output to a JSON file
    public static void writeOrder(Map<Integer, OrderWrapper> orderMap, File outFile) throws IOException {
        JSON.writeValue(outFile, orderMap);

    }
}
