package Project1;

public class Item {
    private String name;
    private int quantity;
    private double price;

    public Item() { }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return quantity; }

    public void setPrice(double price) { this.price = price; }
    public double getPrice() { return price; }

@Override
    public String toString() {
        return  name + "\nquantity = " + quantity + "\nprice = " + price + "\n";
    }
}