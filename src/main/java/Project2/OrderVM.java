package Project2;

import Project1.Item;
import Project1.Order;
import javafx.beans.property.*;

public class OrderVM {
    private final Order order;

    private final IntegerProperty orderId = new SimpleIntegerProperty();
    private final StringProperty  type    = new SimpleStringProperty();
    private final StringProperty  status  = new SimpleStringProperty();
    private final DoubleProperty  total   = new SimpleDoubleProperty();

    public OrderVM(Order order) {
        this.order = order;
        orderId.set(order.getOrderId());
        type.set(order.getType() == null ? "" : order.getType());
        status.set(order.getOrderStatus() == null ? "" : order.getOrderStatus().name());

        double sum = 0.0;
        if (order.getItems() != null) {
            for (Item it : order.getItems()) {
                sum += it.getPrice() * it.getQuantity();
            }
        }
        total.set(sum);
    }

    public Order getOrder() { return order; }

    // Getters for TableView
    public int    getOrderId() { return orderId.get(); }
    public String getType()    { return type.get(); }
    public String getStatus()  { return status.get(); }
    public double getTotal()   { return total.get(); }

    // Property getters for cell value factories
    public IntegerProperty orderIdProperty() { return orderId; }
    public StringProperty  typeProperty()    { return type; }
    public StringProperty  statusProperty()  { return status; }
    public DoubleProperty  totalProperty()   { return total; }
}
