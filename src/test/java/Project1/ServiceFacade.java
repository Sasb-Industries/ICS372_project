package Project1;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ServiceFacadeTest {

    @TempDir
    Path tmp;

    Path base;
    Path inputDir;
    Path acceptedDir;
    Path rejectedDir;
    Path runningListDir;
    Path snapshotFile;

    Object service; // ServiceFacade instance (via reflection)

    // --------  Setup for testing --------

    // Sample map of orders to seed the snapshot
    private Map<Integer, OrderWrapper> sampleOrders() {
        Map<Integer, OrderWrapper> m = new LinkedHashMap<>();

        Order newOrder = new Order();
        newOrder.setOrderStatus(Order.Status.NEW);
        OrderWrapper w1 = new OrderWrapper();
        w1.setOrder(newOrder);
        m.put(7, w1);

        Order started = new Order();
        started.setOrderStatus(Order.Status.STARTED);
        OrderWrapper w2 = new OrderWrapper();
        w2.setOrder(started);
        m.put(42, w2);

        Order done = new Order();
        done.setOrderStatus(Order.Status.COMPLETED);
        OrderWrapper w3 = new OrderWrapper();
        w3.setOrder(done);
        m.put(100, w3);

        return m;
    }

    /** Try both known constructors for ServiceFacade. */
    private Object createServiceFacade() throws Exception {
        Class<?> cls = Class.forName("Project1.ServiceFacade");
        // Prefer the 5-arg form if present
        for (Constructor<?> ctor : cls.getDeclaredConstructors()) {
            Class<?>[] p = ctor.getParameterTypes();
            if (p.length == 5 &&
                    p[0].equals(Path.class) &&
                    p[1].equals(Path.class) &&
                    p[2].equals(Path.class) &&
                    p[3].equals(Path.class) &&
                    p[4].equals(String.class)) {
                return ctor.newInstance(inputDir, acceptedDir, rejectedDir, runningListDir, "orders.json");
            }
        }
        // Fallback to single-arg form: ServiceFacade(Path inputDir)
        Constructor<?> one = cls.getDeclaredConstructor(Path.class);
        return one.newInstance(inputDir);
    }

    private Method findMethod(String name, Class<?>... params) {
        try {
            return Class.forName("Project1.ServiceFacade").getMethod(name, params);
        } catch (Exception e) {
            return null;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        base = tmp.resolve("proj");
        Files.createDirectories(base);

        inputDir = base.resolve("Input Orders");
        acceptedDir = base.resolve("Accepted Orders");
        rejectedDir = base.resolve("Rejected");
        runningListDir = base.resolve("Running List");
        snapshotFile = runningListDir.resolve("orders.json");

        Files.createDirectories(inputDir);
        Files.createDirectories(acceptedDir);
        Files.createDirectories(rejectedDir);
        Files.createDirectories(runningListDir);

        // Seed snapshot with known orders
        Serialization.writeOrder(sampleOrders(), snapshotFile.toFile());

        // Build ServiceFacade
        service = createServiceFacade();
        assertNotNull(service, "ServiceFacade should be constructed");
    }

    @AfterEach
    void tearDown() throws Exception {
        // call close() if available
        Method close = findMethod("close");
        if (close != null) close.invoke(service);
    }

    // ---------- tests ----------

    @Test
    void listAll() throws Exception {
        Method listAll = findMethod("listAll");
        assumeTrue(listAll != null, "ServiceFacade#listAll not found");

        @SuppressWarnings("unchecked")
        Map<Integer, OrderWrapper> all = (Map<Integer, OrderWrapper>) listAll.invoke(service);
        assertNotNull(all, "listAll should not return null");
        assertEquals(3, all.size(), "Should load three seeded orders (7, 42, 100)");
        assertTrue(all.containsKey(7) && all.containsKey(42) && all.containsKey(100), "Expected IDs present");
    }

    @Test
    void writeAllOrders() throws Exception {
        Method writeAllOrders = findMethod("writeAllOrders", File.class);
        assumeTrue(writeAllOrders != null, "ServiceFacade#writeAllOrders(File) not found");

        File out = base.resolve("export.json").toFile();
        writeAllOrders.invoke(service, out);

        assertTrue(out.exists(), "Export file should be created");
        String text = Files.readString(out.toPath(), StandardCharsets.UTF_8);
        assertFalse(text.isBlank(), "Export should contain JSON");
        assertTrue(text.trim().startsWith("{"), "Top-level JSON should be an object (map)");
    }

    @Test
    void refreshFromDisk() throws Exception {
        Method refresh = findMethod("refreshFromDisk");
        Method addListener = findMethod("addListener", OrdersChangedListener.class);
        Method listAll = findMethod("listAll");

        assumeTrue(refresh != null && addListener != null && listAll != null,
                "refreshFromDisk/addListener/listAll not found — skipping");

        // Create a minimal XML order file compatible with your XML mapping
        String xml = """
                <Orders>
                  <Order id="999">
                    <OrderType>Delivery</OrderType>
                    <Item type="Burger"><Price>10.00</Price><Quantity>1</Quantity></Item>
                  </Order>
                </Orders>
                """;
        Path newXml = inputDir.resolve("new_order_999.xml");
        Files.writeString(newXml, xml, StandardCharsets.UTF_8);

        AtomicInteger notifications = new AtomicInteger(0);
        addListener.invoke(service, (OrdersChangedListener) notifications::incrementAndGet);

        // Before refresh
        @SuppressWarnings("unchecked")
        Map<Integer, OrderWrapper> before = (Map<Integer, OrderWrapper>) listAll.invoke(service);
        int beforeSize = before.size();

        refresh.invoke(service);

        // After refresh, we expect either:
        // - the map size increased by 1, and/or
        // - at least one listener notification fired.
        @SuppressWarnings("unchecked")
        Map<Integer, OrderWrapper> after = (Map<Integer, OrderWrapper>) listAll.invoke(service);

        assertTrue(notifications.get() >= 1, "Expected at least one orders-changed notification");
        assertTrue(after.size() >= beforeSize,
                "After refresh, order count should be >= previous (new XML may be accepted)");
        // If accepted, the XML should be moved out of Input Orders
        assertFalse(Files.exists(newXml), "Processed XML should no longer be in Input Orders (moved or removed)");
    }
}
