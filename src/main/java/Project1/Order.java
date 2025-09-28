package Project1;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class Order {
    private List<Item> items;
    private String type;
    private long order_date;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int orderId = 1;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int orderStatus = 1;


    public Order() {}

    public void  setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getOrderId() {
        return orderId;
    }

    public void setOrder_date(long order_date) {
        this.order_date = order_date;
    }
    public long getOrder_date() {
        return order_date;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setItems(List<Item> item){
        this.items = item;
    }
    public List<Item> getItems(){
        return items;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
    public int getOrderStatus() {
        return orderStatus;
    }

    @Override
    public String toString(){
        return "Order " + orderId + "\ntype = " + type + "\norder_date = " + order_date +  "\nITEMS \n " + items;
    }
}
