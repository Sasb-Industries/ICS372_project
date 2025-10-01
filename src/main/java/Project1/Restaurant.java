package Project1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Restaurant {

    private Map<Integer, OrderWrapper> map = new HashMap<Integer, OrderWrapper>();
    private OrderWrapper order;
    private int ID = 1;

    public Restaurant(OrderWrapper order, Map<Integer, OrderWrapper> map) {
        this.order = order;
        this.map = map;
    }

    Scanner scan = new Scanner(System.in);

    public void display(Map <Integer, OrderWrapper> map) {
        System.out.print("Enter Order ID to display: \n");
        int orderID = scan.nextInt();
        for (Item i : map.get(orderID).getOrder().getItems()) {
            System.out.println(i.getName() + " - " + i.getQuantity()+ "\n");
        }
    }
    public void startOrder(Map <Integer, OrderWrapper> map ) {

        // Get order to start from user
        System.out.println("Enter Order ID: ");
        int scanID = scan.nextInt();

        // if (order status = NEW) - start order and set status to started
        if (map.get(scanID).getOrder().getOrderStatus() == Order.Status.NEW) {
            map.get(scanID).getOrder().setOrderStatus(Order.Status.STARTED);
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " is starting.\n");
        } else {
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " is already started or completed.\n");
        }
    }

    public void completeOrder(Map <Integer, OrderWrapper> map) {

        // Have user specify order
        System.out.println("Enter Order ID: ");
        int scanID = scan.nextInt();

        // Swap order specified to completed only if it has been started
        if (map.get(scanID).getOrder().getOrderStatus() == Order.Status.STARTED) {
            map.get(scanID).getOrder().setOrderStatus(Order.Status.COMPLETED);
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " is completed.\n");
        }
        else {
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " has not been started or completed.\n");
        }
    }
    public void incompleteOrder(Map <Integer, OrderWrapper> map) {

        for(OrderWrapper q:  map.values()) {
            if (q.getOrder().getOrderStatus() != Order.Status.COMPLETED) {
                double total = 0;
                for (Item i : q.getOrder().getItems()) {
                    total += i.getPrice();
                }
                System.out.println(q.getOrder().toString() + "\nOrder Total : " + total + "\n");
            }
        }
    }
    public void addOrder(Map <Integer, OrderWrapper> map ) throws IOException {
        System.out.println("Load order");
        String newOrder = scan.next();
        OrderWrapper orderIn = Serialization.readOrder(newOrder);
        ID++;
        map.put(ID, orderIn);
        map.get(ID).getOrder().setOrderId(ID);
    }
}
