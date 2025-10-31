package Project1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Order {

    public enum Status { NEW, STARTED, COMPLETED }

    @JsonProperty("items")                                   // JSON array "items"
    @JacksonXmlElementWrapper(useWrapping = false)           // XML: no <Items> wrapper, just many <Item> elements
    @JacksonXmlProperty(localName = "Item")
    private List<Item> items;
    @JsonProperty("type")                                   // consume JSON field "type"
    @JacksonXmlProperty(localName = "OrderType")            // consume XML element <OrderType>
    @JsonAlias({"OrderType"})
    private String type;

    @JsonProperty("order_date")
    private long orderDate;

    // If a file omits this, ServiceFacade will assign it.
    private int orderId = 0;

    private Status orderStatus = Status.NEW;

    private String source;
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String externalOrderId;

    public Order() { }

    // --- id ---
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getOrderId() { return orderId; }

    // --- type ---
    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    // --- date ---
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    public long getOrderDate() { return orderDate; }

    // --- items ---
    public void setItems(List<Item> items) { this.items = items; }
    public List<Item> getItems() { return items; }

    // --- status ---
    public void setOrderStatus(Status status) { this.orderStatus = status; }
    public Status getOrderStatus() { return orderStatus; }

    // --- source ---
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    // ---external ID ---
    public String getExternalOrderId() { return externalOrderId; }
    public void setExternalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; }

    @Override
    public String toString(){
        return "Order " + orderId + "\ntype = " + type + "\norder_date = " + orderDate +  "\nITEMS \n " + items;
    }
}
