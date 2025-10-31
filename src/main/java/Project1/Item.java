package Project1;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Item {
    @JsonProperty("name")
    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String name;

    @JsonProperty("quantity")
    @JacksonXmlProperty(localName = "Quantity")
    private int quantity;

    @JsonProperty("price")
    @JacksonXmlProperty(localName = "Price")
    private double price;

    public Item() { }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return quantity; }

    public void setPrice(double price) { this.price = price; }
    public double getPrice() { return price; }

@Override
    public String toString() {
        return  name + "\nquantity = " + quantity + "\nprice = " + price + "\n";
    }
}