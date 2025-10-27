package Project1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {
    private final Map<Integer, OrderWrapper> orders;
    private final AtomicInteger nextId;

    public Restaurant(Map<Integer, OrderWrapper> seed, int maxIdSeen) {
        this.orders = new HashMap<>(seed == null ? Map.of() : seed);
        this.nextId = new AtomicInteger(Math.max(1, maxIdSeen + 1));
    }

    // ---------- Commands ----------
    public int add(OrderWrapper ow) {
        if (ow == null || ow.getOrder() == null) return -1;

        Order o = ow.getOrder();
        int id = o.getOrderId();

        if (id <= 0) {
            id = nextId.getAndIncrement();
            o.setOrderId(id);
        } else {
            final int accepted = id;
            nextId.updateAndGet(n -> Math.max(n, accepted + 1));
        }
        orders.put(id, ow);
        return id;
    }

    /** Replace by id if present; if no id, behaves like add(). */
    public void upsertReplacingById(OrderWrapper ow) {
        if (ow == null || ow.getOrder() == null) return;
        int id = ow.getOrder().getOrderId();
        if (id <= 0) {
            add(ow);
        } else {
            orders.put(id, ow);
            final int accepted = id;
            nextId.updateAndGet(n -> Math.max(n, accepted + 1));
        }
    }

    public void start(int id) {
        OrderWrapper w = orders.get(id);
        if (w != null && w.getOrder().getOrderStatus() == Order.Status.NEW) {
            w.getOrder().setOrderStatus(Order.Status.STARTED);
        }
    }

    public void complete(int id) {
        OrderWrapper w = orders.get(id);
        if (w != null && w.getOrder().getOrderStatus() == Order.Status.STARTED) {
            w.getOrder().setOrderStatus(Order.Status.COMPLETED);
        }
    }

    public boolean deleteIfNew(int id) {
        OrderWrapper w = orders.get(id);
        if (w != null && w.getOrder().getOrderStatus() == Order.Status.NEW) {
            orders.remove(id);
            return true;
        }
        return false;
    }

    // ---------- Queries (no printing) ----------
    public Map<Integer, OrderWrapper> all() {
        return new HashMap<>(orders);
    }

    public OrderWrapper get(int id) {
        return orders.get(id);
    }

    public List<OrderWrapper> incomplete() {
        List<OrderWrapper> out = new ArrayList<>();
        for (OrderWrapper w : orders.values()) {
            if (w.getOrder().getOrderStatus() != Order.Status.COMPLETED) out.add(w);
        }
        return out;
    }

    public double totalFor(int id) {
        OrderWrapper w = orders.get(id);
        if (w == null) return 0.0;
        double sum = 0.0;
        for (Item it : w.getOrder().getItems()) {
            sum += it.getPrice() * it.getQuantity();
        }
        return sum;
    }

    public int maxId() {
        return orders.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}
