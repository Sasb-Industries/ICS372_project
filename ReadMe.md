# README.md

## FoodHub Order Tracker (ICS 372 – Group Assignment 1)

A desktop Java application that helps restaurant staff manage online orders received via a 3rd‑party provider (FoodHub). Staff can import orders from JSON, view/start/complete them, and export all orders back to JSON. Includes a simple GUI (JavaFX).

> Built with Java 21+ and JavaFX. No database — data is kept in memory for this assignment; import/export handles persistence.

### Features

* Import orders from JSON
* Support **two order types**: `togo` and `pickup`
* Track order attributes: **type**, **time**, **items** (name, quantity, price), and **orderId**
* Commands per order: **display**, **start**, **complete**
* Keep a **record of completed** orders
* Export **all** orders to a single JSON file
* Show **all uncompleted** orders with **price totals**

### Screens (JavaFX)

* **Incoming Orders**: table of uncompleted orders (columns: Order ID, Type, Placed Time, Items Count, Total, Status, Actions)
* **Order Details**: itemized list, totals, actions (Start/Complete)
* **Import/Export Dialogs**: choose JSON file to load/save

### JSON Format

**Input:**

```json
{
  "order": {
    "type": "togo",
    "order_date": 1515354694451,
    "items": [
      {"name": "Burger", "quantity": 1, "price": 8.99},
      {"name": "Fries",  "quantity": 2, "price": 3.99},
      {"name": "Milkshake","quantity": 1, "price": 8.95}
    ]
  }
}
```

### Usage

* **File → Import…**: pick input JSON (single order or `{"orders": [...]}`)
* **Orders Table**: select an order → **Start** or **Complete**
* **View → Show Uncompleted**: shows totals
* **File → Export…**: write `orders.json`

### Design Notes

* **State rules**: `NEW → STARTED → COMPLETED` (no skipping; no re‑start/complete twice)
* **Totals**: `sum(quantity * price)`; show subtotal per order
* **Time**: store epoch millis; display human‑readable in UI
* **IDs**: generate `FH-YYYY-#####` when missing in input
* **Validation**: quantities ≥ 1; price ≥ 0

### Deliverables Checklist

* ✅ Working app with UI
* ✅ Class diagram
* ✅ Sequence diagram (add incoming order)

### Testing

* Unit tests for `OrderService` (state transitions, totals)
* Parser tests for JSON import/export (happy/edge cases)

### Group Memebers
| Name           | TechID   |
|----------------|----------|
| Theophilus Cox | 16490348 |
| Chee Nu Xiong  | ya7383we |
| Sasha Johnson  | vh1795lr |
| Group Member 4 | 000000   |
| Kyle Durbin    | 16736862 |

