package Project1;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


/** Minimal wrapper that matches the file format:
 *  { "order": { ... } }
 */
@JacksonXmlRootElement(localName = "Orders")
public class OrderWrapper {
    @JsonProperty("order")                          // JSON: { "order": { ... } }
    @JacksonXmlProperty(localName = "Order")
    private Order order;

    public OrderWrapper() { }

    public void setOrder(Order order) { this.order = order; }
    public Order getOrder() { return order; }


@Override
    public String toString() {
        return " This is the " + order;
    }
}