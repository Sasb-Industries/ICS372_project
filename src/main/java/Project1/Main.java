package Project1;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {

        // Create scanner and prompt user for file path ( src/main/resources/testOrder.json )
        Scanner scan = new Scanner(System.in);
        System.out.println("Load first order");
        String firstOrder = scan.nextLine();

        // Read in order and place in hash map - passing in a filepath
        OrderWrapper orderIn = Serialization.readOrder(firstOrder);
        Map <Integer, OrderWrapper> map = new HashMap<>();

        // Create our "Restaurant" and provide user with options
        Resturant resturant = new Resturant(orderIn, map);
        map.put(1 , orderIn);
        printOpts();

        // User interaction logic
        int choice = scan.nextInt();
        while (choice !=7 ) {

            // Start an Order - will enter integer for orderID
            if (choice == 1) {
                scan.nextLine();
                resturant.startOrder(map);
                printOpts();
                choice = scan.nextInt();
            }

            // Complete an order - will enter integer for orderID
            else if (choice == 2) {
                resturant.completeOrder(map);
                printOpts();
                choice = scan.nextInt();
            }

            // Display order (in console) - will enter integer for orderID
            else if (choice == 3) {
                resturant.display(map);
                printOpts();
                choice = scan.nextInt();
            }

            // Display all incomplete orders
            else if (choice == 4) {
                scan.nextLine();
                resturant.incompleteOrder(map);
                printOpts();
                choice = scan.nextInt();
            }

            // Print all orders to an external json file
            else if (choice == 5) {
                Serialization.writeOrder(map, new File("orders_by_id.json"));
                System.out.println("File Exported Successfully");
                printOpts();
                choice = scan.nextInt();
            }

            //  Add an order - another JSON file - NOT SUPPORTED
            else if (choice == 6) {
                resturant.addOrder(map);
                printOpts();
                choice = scan.nextInt();

            }

            // User is f'ing with us
            else{
                System.out.println("Invalid choice. Try again.");
                printOpts();
                choice = scan.nextInt();
            }
        }

        if (choice == 7) {
            System.out.println("Have a great night, goodbye!");
            scan.close();
        }
    }

    public static void printOpts() {
        System.out.println("What would you like to do?\n" +
                            "1: Start order\n" +
                            "2: Complete Order \n" +
                            "3: Display an Order\n" +
                            "4: Display Incomplete Orders\n" +
                            "5: Export All Orders\n" +
                            "6: Add new order\n" +
                            "7: Close up Shop");
    }
}