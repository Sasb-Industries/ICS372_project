package Project1;

public class OrderWrapper {
    Order order;
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    @Override
    public String toString() {
        return " This is the " + order;
    }
}

