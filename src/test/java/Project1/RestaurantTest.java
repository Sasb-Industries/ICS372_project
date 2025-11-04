package Project1;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the Restaurant class.

class RestaurantTest {

    // ----- Mocking an object - probably should use Mockito -----

    //Builds an Item with the given name, price, and quantity
    private static Item item(String name, double price, int qty) {
        Item i = new Item();
        i.setName(name);
        i.setPrice(price);
        i.setQuantity(qty);
        return i;
    }

    // Builds an OrderWrapper containing a single Order with given id, status, and items
    private static OrderWrapper orderWrapper(int id, Order.Status status, List<Item> items) {
        Order o = new Order();
        o.setOrderId(id);
        o.setItems(items);
        o.setOrderStatus(status);
        OrderWrapper w = new OrderWrapper();
        w.setOrder(o);
        return w;
    }

    // Restaurant with no seed data and maxIdSeen=0 - Start tests with this "clean"
    private static Restaurant emptyRestaurant() {
        return new Restaurant(Map.of(), 0);
    }

    // -------- Begin actual unit tests --------

    // Testing when OrderID is 0 and added
    @Test
    void add_orderIDisZero() {
        Restaurant r = emptyRestaurant();

        // Create an order with id=0 (meaning "needs assignment")
        OrderWrapper w = orderWrapper(0, Order.Status.NEW, List.of(item("Burger", 10.0, 2)));
        int assignedId = r.add(w);

        assertTrue(assignedId > 0, "Restaurant should assign a positive ID");
        OrderWrapper stored = r.get(assignedId);
        assertNotNull(stored, "Order should be retrievable after add");
        assertEquals(assignedId, stored.getOrder().getOrderId(), "Stored order must reflect assignedId");
        assertEquals(Order.Status.NEW, stored.getOrder().getOrderStatus(), "Newly added orders should remain NEW");
    }

    // Adding order that already has an ID - it needs to stay
    @Test
    void add_hasExistingID() {
        Restaurant r = emptyRestaurant();

        OrderWrapper w1 = orderWrapper(7, Order.Status.NEW, List.of(item("Fries", 3.0, 1)));
        int id1 = r.add(w1);

        assertEquals(7, id1, "If an ID is pre-set, Restaurant should keep it");
        assertEquals(7, r.maxId(), "maxId should reflect highest ID present");
        assertNotNull(r.get(7), "Order with given ID should be stored");
    }

    // testing that a NEW order transitions to STARTED and ONLY STARTED
    @Test
    void start_newToStarted() {
        Restaurant r = emptyRestaurant();

        // test cases for each status
        int idNew = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("A", 1, 1))));
        int idStarted = r.add(orderWrapper(0, Order.Status.STARTED, List.of(item("B", 1, 1))));
        int idCompleted = r.add(orderWrapper(0, Order.Status.COMPLETED, List.of(item("C", 1, 1))));

        // NEW -> STARTED
        r.start(idNew);
        assertEquals(Order.Status.STARTED, r.get(idNew).getOrder().getOrderStatus(),
                "start() should progress order, NEW -> STARTED");

        // STARTED -> (no change)
        r.start(idStarted);
        assertEquals(Order.Status.STARTED, r.get(idStarted).getOrder().getOrderStatus(),
                "start() should not change an already STARTED order");

        // COMPLETED -> (no change)
        r.start(idCompleted);
        assertEquals(Order.Status.COMPLETED, r.get(idCompleted).getOrder().getOrderStatus(),
                "start() should not reset a COMPLETED order");
    }

    // testing that a STARTED order transitions to COMPLETED and ONLY COMPLETED
    @Test
    void complete_startedToCompleted() {
        Restaurant r = emptyRestaurant();

        // test cases
        int idNew = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("A", 1, 1))));
        int idStarted = r.add(orderWrapper(0, Order.Status.STARTED, List.of(item("B", 1, 1))));
        int idCompleted = r.add(orderWrapper(0, Order.Status.COMPLETED, List.of(item("C", 1, 1))));

        // STARTED -> COMPLETED (Ideal case)
        r.complete(idStarted);
        assertEquals(Order.Status.COMPLETED, r.get(idStarted).getOrder().getOrderStatus(),
                "complete() should progress order, STARTED -> COMPLETED");

        // NEW -> (no change)
        r.complete(idNew);
        assertEquals(Order.Status.NEW, r.get(idNew).getOrder().getOrderStatus(),
                "complete() should not move NEW directly to COMPLETED");

        // COMPLETED -> (no change)
        r.complete(idCompleted);
        assertEquals(Order.Status.COMPLETED, r.get(idCompleted).getOrder().getOrderStatus(),
                "complete() should not change an already COMPLETED order");
    }


    // Testing for order deletion ONLY if order.status = NEW
    // Once an order is started you cannot delete it
    @Test
    void deleteIfNew() {
        Restaurant r = emptyRestaurant();

        int idNew = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("A", 1, 1))));
        int idStarted = r.add(orderWrapper(0, Order.Status.STARTED, List.of(item("B", 1, 1))));
        int idCompleted = r.add(orderWrapper(0, Order.Status.COMPLETED, List.of(item("C", 1, 1))));

        // delete and erase NEW order from system
        assertTrue(r.deleteIfNew(idNew), "Should delete when order is NEW");
        assertNull(r.get(idNew), "Deleted NEW order should no longer be retrievable");

        // Should NOT delete and NOT erase order if status is STARTED
        assertFalse(r.deleteIfNew(idStarted), "Should not delete when order is STARTED");
        assertNotNull(r.get(idStarted));

        // Should NOT delete and NOT erase order if order status = COMPLETED
        assertFalse(r.deleteIfNew(idCompleted), "Should not delete when order is COMPLETED");
        assertNotNull(r.get(idCompleted));

        // Garbage input
        assertFalse(r.deleteIfNew(123456), "Non-existent IDs should return false");
    }


    // totalFor(id) math checking
    // sum = price * quantity
    @Test
    void totalFor() {
        Restaurant r = emptyRestaurant();

        int id = r.add(orderWrapper(0, Order.Status.NEW, List.of(
                item("Burger", 10.0, 2),   // 20.0 total
                item("Fries",  3.0,  1),   // 3.0 total
                item("Soda",   2.5,  2)    // 5.0 total
        )));

        assertEquals(28.0, r.totalFor(id), 0.01, "Should sum price*qty across all items");

        assertEquals(0.0, r.totalFor(99999), 0.01, "Unknown id should return 0.0");
    }


    //  Maintains current state and is keyed by orderID
    @Test
    void allAndGet() {
        Restaurant r = emptyRestaurant();

        int a = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("A", 1, 1))));
        int b = r.add(orderWrapper(0, Order.Status.STARTED, List.of(item("B", 1, 1))));

        Map<Integer,OrderWrapper> m = r.all();
        assertTrue(m.containsKey(a));
        assertTrue(m.containsKey(b));
        assertEquals(a, m.get(a).getOrder().getOrderId());

        assertNotNull(r.get(b));
        assertNull(r.get(123456)); // bogus value
    }


    // Incomplete() should return all orders that are NOT COMPLETED (NEW or STARTED)
    @Test
    void incomplete() {
        Restaurant r = emptyRestaurant();

        int idNew = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("A", 1, 1))));
        int idStarted = r.add(orderWrapper(0, Order.Status.STARTED, List.of(item("B", 1, 1))));
        int idCompleted = r.add(orderWrapper(0, Order.Status.COMPLETED, List.of(item("C", 1, 1))));

        // collect incomplete orders - should be A, B
        List<OrderWrapper> inc = r.incomplete();
        var ids = inc.stream().map(ow -> ow.getOrder().getOrderId()).toList();

        assertTrue(ids.contains(idNew), "NEW should be included");
        assertTrue(ids.contains(idStarted), "STARTED should be included");
        assertFalse(ids.contains(idCompleted), "COMPLETED should be excluded");
    }


    @Test
    void maxId() {
        Restaurant r = emptyRestaurant();

        // when no orders - need to return 0
        assertEquals(0, r.maxId(), "Empty Restaurant should report maxId=0");

        int a = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("A", 1, 1))));
        int b = r.add(orderWrapper(0, Order.Status.NEW, List.of(item("B", 1, 1))));
        int c = r.add(orderWrapper(99, Order.Status.NEW, List.of(item("C", 1, 1)))); // explicit ID

        assertEquals(Math.max(Math.max(a, b), c), r.maxId(), "maxId should return the highest actual key");
    }
}
