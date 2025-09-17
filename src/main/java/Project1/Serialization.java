package Project1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class Serialization {
    private static final ObjectMapper mapper = new ObjectMapper();
    public Serialization() {}

    public static OrderWrapper readOrder() throws FileNotFoundException {
        try(InputStream in = Serialization.class.getResourceAsStream("/testOrder.json")){
            if (in == null){
                throw  new FileNotFoundException("File not found");
            }
            return mapper.readValue(in, OrderWrapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeOrder(OrderWrapper order) throws IOException {

    }
}
