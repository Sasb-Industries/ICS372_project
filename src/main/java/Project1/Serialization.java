package Project1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Class for converting JSON into Java objects that we can use
// We used JACKSON for our JSON processing
public final class JsonSerialization {

    // Mapper is provided by Jackson
    private static final ObjectMapper mapper = new ObjectMapper();

    public Serialization() {}

    public static OrderWrapper readOrder(String pathStr) throws IOException {

        // Filepath as a string
        Path path = Paths.get(pathStr);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + path.toAbsolutePath());
        }
        try (InputStream in = Files.newInputStream(path)) {
            return mapper.readValue(in, OrderWrapper.class);
        }
    }

    // Takes our hashmap and writes the output to a JSON file
    public static void writeOrder(Map<Integer, OrderWrapper> orderMap, File outFile) throws IOException {
        mapper.writeValue(outFile, orderMap);

    }
}
