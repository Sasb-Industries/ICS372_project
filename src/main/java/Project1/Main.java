package Project1;
import java.io.IOException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Load first order");
        String firstOrder = scan.nextLine();
        OrderWrapper orderIn = Serialization.readOrder(firstOrder);
        Resturant resturant = new Resturant(orderIn);
        OrderWrapperList list = new OrderWrapperList();
        list.addOrder(orderIn);
        printOpts();

        int choice = scan.nextInt();
        while (choice !=7 ) {
            if (choice == 1) {
                resturant.startOrder();
                printOpts();
                choice = scan.nextInt();
            }
            else if (choice == 2) {
                resturant.completeOrder();
                printOpts();
                choice = scan.nextInt();
            }
            else if (choice == 3) {
                resturant.display();
                printOpts();
                choice = scan.nextInt();
            }
            else if (choice == 4) {
                System.out.println(resturant.incompleteOrder());
                printOpts();
                choice = scan.nextInt();
            }
            else if (choice == 5) {

            }
            else if (choice == 6) {
                scan.nextLine();
                System.out.println("Load order");
                String newOrder = scan.nextLine();
                OrderWrapper anotherOrder = Serialization.readOrder(newOrder);
                resturant = new Resturant(anotherOrder);
                list.addOrder(anotherOrder);
                printOpts();
                choice = scan.nextInt();

            }
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
        System.out.println("What would you like to do? \n1: Start order");
        System.out.print("2: Complete Order \n3: Display an Order");
        System.out.println("\n4: Display Incomplete Orders \n5: Print All Orders");
        System.out.println("6: Add new order");
        System.out.println("7: Close up Shop");
    }
}