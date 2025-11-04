package Project1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// All unit tests for the Item class

class ItemTest {

    @Test
    void gettersAndSettersTest() {
        Item it = new Item();

        it.setName("Burger");
        it.setQuantity(3);
        it.setPrice(10.50);

        assertEquals("Burger", it.getName());
        assertEquals(3, it.getQuantity());
        assertEquals(10.50, it.getPrice(), 1e-9);
    }

    @Test
    void defaultValues() {
        Item it = new Item();
        assertNull(it.getName());
        assertEquals(0, it.getQuantity());
        assertEquals(0.0, it.getPrice(), 1e-9);
    }

    @Test
    void jacksonXml_deserializesAttributeAndElementsCorrectly() throws Exception {
        // Based on annotations in Item:
        // name -> @JacksonXmlProperty(isAttribute = true, localName = "type")
        // quantity -> <Quantity>...</Quantity>
        // price -> <Price>...</Price>
        String xml = "<Item type=\"Pizza\"><Quantity>4</Quantity><Price>12.75</Price></Item>";

        XmlMapper xmlMapper = new XmlMapper();
        Item it = xmlMapper.readValue(xml, Item.class);

        assertEquals("Pizza", it.getName());
        assertEquals(4, it.getQuantity());
        assertEquals(12.75, it.getPrice(), 1e-9);
    }

    @Test
    void jacksonJson_serializesWithExpectedPropertyNames() throws Exception {
        Item it = new Item();
        it.setName("Salad");
        it.setQuantity(1);
        it.setPrice(6.0);

        ObjectMapper json = new ObjectMapper();
        String jsonStr = json.writeValueAsString(it);

        // Annotations use @JsonProperty("name"/"quantity"/"price")
        assertTrue(jsonStr.contains("\"name\":\"Salad\""));
        assertTrue(jsonStr.contains("\"quantity\":1"));
        assertTrue(jsonStr.contains("\"price\":6.0"));
    }
}
