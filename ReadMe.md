# Restaurant Orders

A simple Java program for managing restaurant orders. Orders are read from JSON files, stored in memory, and can be started, completed, or displayed.

## Classes
- **Main** – entry point, shows menu.
- **Restaurant** – manages the orders.
- **Serialization** – loads/saves orders from JSON.
- **OrderWrapper** – wraps a single `Order`.
- **Order** – order details (id, type, status, items).
- **Item** – item details (name, quantity, price).

## Running
1. Open the project in your IDE (e.g., IntelliJ, Eclipse, VS Code).
2. Set `Main.java` as the run configuration.
3. Run the program.

## Usage
When the program starts, a menu will be displayed:

- **Add Order** → type the option number, then enter the JSON file path for the order.
- **Start Order** → enter the option, then the order ID to mark it as *started*.
- **Complete Order** → enter the option, then the order ID to mark it as *completed*.
- **Display Order** → enter the option, then the order ID to list all items.
- **Exit** → choose the exit option to quit.

Orders must be defined in JSON files before being loaded.
