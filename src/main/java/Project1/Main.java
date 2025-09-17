package Project1;
import java.io.IOException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {
        OrderWrapper orderIn = Serialization.readOrder();

        Scanner scan = new Scanner(System.in);

        System.out.println("What would you like to do? \n 1: Start order\n");
        System.out.print("2: Complete Order \n3: Display an Order");
        System.out.println("4: Display Incomplete Orders \n5: Print All Orders");
        System.out.println("6: Close up Shop");
        int choice = scan.nextInt();
        if (choice == 1) {
            System.out.println("Enter Order ID: ");
            int scanID = scan.nextInt();
            if (scanID == orderIn.getOrder().getOrderId() && orderIn.getOrder().getOrderStatus() == 1) {
                orderIn.getOrder().setOrderStatus(2);
                System.out.println("Order ID: " + orderIn.getOrder().getOrderId() + " is starting.");
            } else {
                System.out.println("Order ID: " + orderIn.getOrder().getOrderId() + " is already started or completed.");
            }

        } else if (choice == 2) {
            System.out.println("Enter Order ID: ");
            int scanID = scan.nextInt();
            if (scanID == orderIn.getOrder().getOrderId() && orderIn.getOrder().getOrderStatus() == 2) {
                orderIn.getOrder().setOrderStatus(3);
                System.out.println("Order ID: " + orderIn.getOrder().getOrderId() + " is completed.");
            }
        } else if (choice == 3) {

            for (Item i : orderIn.getOrder().getItems()) {
                System.out.println(i.getName() + " - " + i.getPrice());
            }
        } else if (choice == 4) {

        } else if (choice == 5) {

        } else {
        }
    }
}