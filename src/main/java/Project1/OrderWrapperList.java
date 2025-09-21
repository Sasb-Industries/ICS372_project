package Project1;

import java.util.HashMap;
import java.util.Map;

public class OrderWrapperList {
    Map<Integer, OrderWrapper> list = new HashMap<>();
    OrderWrapper orders;

public OrderWrapperList() {}
    public Map<Integer, OrderWrapper> getList() {
        return list;
    }
    public void addOrder(OrderWrapper order ) {
        int orderID = order.getOrder().getOrderId();
        if (list.containsKey(orderID)) {
            while(list.containsKey(orderID)) {
                orderID++;
            }
        }
        order.getOrder().setOrderId(orderID);
        list.put(orderID, order);
    }
}

