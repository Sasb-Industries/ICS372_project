package Project1;

/** Minimal wrapper that matches the file format:
 *  { "order": { ... } }
 */
public class OrderWrapper {
    private Order order;

    public OrderWrapper() { }

    public void setOrder(Order order) { this.order = order; }
    public Order getOrder() { return order; }


@Override
    public String toString() {
        return " This is the " + order;
    }
}