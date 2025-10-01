package Project1;
import java.io.File;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {

        // Create scanner and prompt user for file path ( src/main/resources/testOrder.json )
        try (Scanner scan = new Scanner(System.in)) {
            System.out.println("Load first order (e.g. src/main/resources/testOrder.json)");
            String firstOrder = scan.nextLine();

            // Check that filepath is valid
            OrderWrapper orderIn;
            try {
                orderIn = Serialization.readOrder(firstOrder);
            } catch (Exception e) {
                System.err.println("File not found exception:\n" +
                                    e.getMessage() +
                                    "\nHINT: You probably fudged the filepath");
                return;
            }

            // place our order into our map
            Map<Integer, OrderWrapper> map = new HashMap<>();
            map.put(1, orderIn);

            // Create our "Restaurant" and provide it with the order map
            Restaurant restaurant = new Restaurant(orderIn, map);

            // Start program loop
            boolean running = true;
            while (running) {
                printOpts();

                // Test input
                if (!scan.hasNextInt()) {
                    scan.nextLine();
                    System.out.print("Enter a number : ");
                }

                int choice = scan.nextInt();
                scan.nextLine();  // Consume newline character

                switch (choice) {
                    case 1:
                        restaurant.startOrder(map);
                        break;
                    case 2:
                        restaurant.completeOrder(map);
                        break;
                    case 3:
                        restaurant.display(map);
                        break;
                    case 4:
                        restaurant.incompleteOrder(map);
                        break;
                    case 5:
                        restaurant.addOrder(map);
                        break;
                    case 6:
                        System.out.println("Have a great night, goodbye!");
                        try {
                            Serialization.writeOrder(map, new File("orders_by_id.json"));
                            System.out.println("File Exported Successfully");
                        } catch (Exception e) {
                            System.err.println("Export failed: " + e.getMessage());
                        }
                        running = false;
                        break;
                    default:
                        System.out.println("Wrong choice");
                        break;
                }
            }
        }
    }

    public static void printOpts() {
        System.out.println("""
                What would you like to do?
                1: Start order
                2: Complete Order
                3: Display an Order
                4: Display Incomplete Orders
                5: Add new order
                6: Close up Shop
                """);
    }
}