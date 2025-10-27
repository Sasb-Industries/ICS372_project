package Project1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Order {

    public enum Status { NEW, STARTED, COMPLETED }

    private List<Item> items;
    private String type;

    @JsonProperty("order_date")
    private long orderDate;

    // If a file omits this, ServiceFacade will assign it.
    private int orderId = 0;

    private Status orderStatus = Status.NEW;

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


@Override
    public String toString(){
        return "Order " + orderId + "\ntype = " + type + "\norder_date = " + orderDate +  "\nITEMS \n " + items;
    }
}
