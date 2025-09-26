package Project1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Resturant {
    private Map<Integer, OrderWrapper> map = new HashMap<Integer, OrderWrapper>();
    private OrderWrapper order;
    private int ID = 1;
    public Resturant(OrderWrapper order, Map<Integer, OrderWrapper> map) {
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
        System.out.println("Enter Order ID: ");
        int scanID = scan.nextInt();

        if (map.get(scanID).getOrder().getOrderStatus() == 1) {
            map.get(scanID).getOrder().setOrderStatus(2);
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " is starting.\n");
        } else {
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " is already started or completed.\n");
        }
    }
    public void completeOrder(Map <Integer, OrderWrapper> map) {
        System.out.println("Enter Order ID: ");
        int scanID = scan.nextInt();
        if (map.get(scanID).getOrder().getOrderStatus() == 2) {
            map.get(scanID).getOrder().setOrderStatus(3);
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " is completed.\n");
        }
        else {
            System.out.println("Order ID: " + map.get(scanID).getOrder().getOrderId() + " has not been started or completed.\n");
        }
    }
    public void incompleteOrder(Map <Integer, OrderWrapper> map) {

        for(OrderWrapper q:  map.values()){
            if(q.getOrder().getOrderStatus() != 3){
                double total = 0;
                for(Item i : q.getOrder().getItems()) {
                    total += i.getPrice();
                }
                System.out.println(q.getOrder().toString() + "\nOrder Total : " + total + "\n");
            }
        }
    }
    public void addOrder(Map <Integer, OrderWrapper> map ) throws IOException {
        System.out.println("Load order");
        String newOrder = scan.nextLine();
        OrderWrapper orderIn = Serialization.readOrder(newOrder);
        ID++;
        map.put(ID, orderIn);
        map.get(ID).getOrder().setOrderId(ID);
    }

}
