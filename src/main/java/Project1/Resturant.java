package Project1;

import java.util.Scanner;

public class Resturant {
    private OrderWrapper order;
    public Resturant(OrderWrapper order) {
        this.order = order;
    }
    Scanner scan = new Scanner(System.in);

    public void display() {
        for (Item i : order.getOrder().getItems()) {
            System.out.println(i.getName() + " - " + i.getQuantity());
        }
    }
    public void startOrder() {
        System.out.println("Enter Order ID: ");
        int scanID = scan.nextInt();
        if (scanID == order.getOrder().getOrderId() && order.getOrder().getOrderStatus() == 1) {
            order.getOrder().setOrderStatus(2);
            System.out.println("Order ID: " + order.getOrder().getOrderId() + " is starting.");
        } else {
            System.out.println("Order ID: " + order.getOrder().getOrderId() + " is already started or completed.");
        }
    }
    public void completeOrder() {
        System.out.println("Enter Order ID: ");
        int scanID = scan.nextInt();
        if (scanID == order.getOrder().getOrderId() && order.getOrder().getOrderStatus() == 2) {
            order.getOrder().setOrderStatus(3);
            System.out.println("Order ID: " + order.getOrder().getOrderId() + " is completed.");
        }
        else {
            System.out.println("Order ID: " + order.getOrder().getOrderId() + " has not been started or completed.");
        }
    }
}
