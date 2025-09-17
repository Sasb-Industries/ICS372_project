package Project1;
import java.util.List;

public class Item {
    private List<Item> items;
    private String name;
    private int quantity;
    private double price;
    public Item(){}

    public void setItems(List<Item> items) {
        this.items = items;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getPrice() {
        return price;
    }
    @Override
    public String toString() {
        return  name + "\nquantity = " + quantity + "\nprice = " + price + "\n";
    }
}
