package Project1;

import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class RestaurantPrinter {
    private final Restaurant restaurant;
    private final PrintStream out;

    public RestaurantPrinter(Restaurant restaurant) {
        this(restaurant, System.out);
    }

    public RestaurantPrinter(Restaurant restaurant, PrintStream out) {
        this.restaurant = restaurant;
        this.out = out == null ? System.out : out;
    }

    /** Display a specific order by id (items + total). */
    public void display(int orderId) {
        OrderWrapper w = restaurant.get(orderId);
        if (w == null || w.getOrder() == null) {
            out.println("Order ID " + orderId + " not found.\n");
            return;
        }
        Order o = w.getOrder();

        out.println("Order ID: " + o.getOrderId() + "  Status: " + o.getOrderStatus());
        for (Item it : o.getItems()) {
            out.println(" • " + it.getName() + " x" + it.getQuantity() + " @ " + it.getPrice());
        }
        out.println("Order Total: " + restaurant.totalFor(orderId));
        out.println();
    }

    /** Print all incomplete (non-COMPLETED) orders in a compact way. */
    public void printIncompleteOrders() {
        List<OrderWrapper> list = restaurant.incomplete();
        if (list.isEmpty()) {
            out.println("No incomplete orders.\n");
            return;
        }
        out.println("Incomplete Orders:");
        for (OrderWrapper w : list) {
            Order o = w.getOrder();
            out.println(" - ID " + o.getOrderId() + "  Status: " + o.getOrderStatus()
                    + "  Items: " + o.getItems().size());
        }
        out.println();
    }

    /**
     * Writes all current orders to the given file as JSON using Serialization#writeOrder(Map, File)}.
     * Also logs a short confirmation message to the configured PrintStream.
     *
     * @param outFile destination file (will be overwritten)
     * @throws IOException if writing fails
     */
    public void writeAllOrdersToFile(File outFile) throws IOException {
        if (outFile == null) throw new IllegalArgumentException("outFile cannot be null");
        Serialization.writeOrder(restaurant.all(), outFile);
        out.println("Wrote " + restaurant.all().size() + " order(s) to: " + outFile.getAbsolutePath());
        out.flush();
    }
}

