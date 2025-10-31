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
    private final StringProperty source   = new SimpleStringProperty();
    private final StringProperty extId    = new SimpleStringProperty();

    public OrderVM(Order order) {
        this.order = order;
        orderId.set(order.getOrderId());
        type.set(order.getType());
        status.set(order.getOrderStatus().name());
        total.set(order.getItems() == null ? 0.0 :
                order.getItems().stream().mapToDouble(it -> it.getPrice() * it.getQuantity()).sum());

        source.set(order.getSource() == null ? "" : order.getSource());
        extId.set(order.getExternalOrderId() == null ? "" : order.getExternalOrderId());
    }

    public Order getOrder() { return order; }

    // Getters for TableView
    public String getSource() { return source.get(); }
    public String getExtId()  { return extId.get(); }
    public int    getOrderId() { return orderId.get(); }
    public String getType()    { return type.get(); }
    public String getStatus()  { return status.get(); }
    public double getTotal()   { return total.get(); }

    // Property getters for cell value factories
    public StringProperty sourceProperty() { return source; }
    public StringProperty extIdProperty()  { return extId; }
    public IntegerProperty orderIdProperty() { return orderId; }
    public StringProperty  typeProperty()    { return type; }
    public StringProperty  statusProperty()  { return status; }
    public DoubleProperty  totalProperty()   { return total; }
}
