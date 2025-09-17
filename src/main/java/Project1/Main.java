package Project1;
import java.io.IOException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {
        OrderWrapper orderIn = Serialization.readOrder();
        Resturant resturant = new Resturant(orderIn);
        Scanner scan = new Scanner(System.in);

        System.out.println("What would you like to do? \n1: Start order");
        System.out.print("2: Complete Order \n3: Display an Order");
        System.out.println("\n4: Display Incomplete Orders \n5: Print All Orders");
        System.out.println("6: Close up Shop");
        int choice = scan.nextInt();
        while (choice !=6 ) {
            if (choice == 1) {
                resturant.startOrder();
                System.out.println("What would you like to do? \n1: Start order");
                System.out.print("2: Complete Order \n3: Display an Order");
                System.out.println("\n4: Display Incomplete Orders \n5: Print All Orders");
                System.out.println("6: Close up Shop");
                choice = scan.nextInt();
            }
            else if (choice == 2) {
                resturant.completeOrder();
                System.out.println("What would you like to do? \n1: Start order");
                System.out.print("2: Complete Order \n3: Display an Order");
                System.out.println("\n4: Display Incomplete Orders \n5: Print All Orders");
                System.out.println("6: Close up Shop");
                choice = scan.nextInt();
            }
            else if (choice == 3) {
                resturant.display();
                System.out.println("What would you like to do? \n1: Start order");
                System.out.print("2: Complete Order \n3: Display an Order");
                System.out.println("\n4: Display Incomplete Orders \n5: Print All Orders");
                System.out.println("6: Close up Shop");
                choice = scan.nextInt();
            }
            else if (choice == 4) {
                System.out.println(resturant.incompleteOrder());
                System.out.println("What would you like to do? \n1: Start order");
                System.out.print("2: Complete Order \n3: Display an Order");
                System.out.println("\n4: Display Incomplete Orders \n5: Print All Orders");
                System.out.println("6: Close up Shop");
                choice = scan.nextInt();
            }
            else if (choice == 5) {

            }

            else{
                System.out.println("Invalid choice. Try again.");
            }
        }
        if (choice == 6) {
            System.out.println("Have a great night, goodbye!");
            scan.close();
        }
    }
}