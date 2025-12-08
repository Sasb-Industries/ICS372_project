package Project2;

import Project1.*;
import Project2.NewOrderController;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainController {
    @FXML private Label statusLabel;
    @FXML private TextField orderIdField;
    @FXML private TableView<OrderVM> ordersTable;
    @FXML private TableColumn<OrderVM, Number> colId;
    @FXML private TableColumn<OrderVM, String> colType;
    @FXML private TableColumn<OrderVM, String> colStatus;
    @FXML private TableColumn<OrderVM, Number> colTotal;
    @FXML private TableColumn<OrderVM, String> colSource;
    @FXML private TableColumn<OrderVM, String> colExtId;
    @FXML private ListView<String> itemsList;

    private final ObservableList<OrderVM> rows = FXCollections.observableArrayList();
    private ServiceFacade service;

    @FXML private void initialize() {
        colId.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getOrderId()));
        colType.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getType()));
        colStatus.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getStatus()));
        colTotal.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getTotal()));
        colSource.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getSource()));
        colExtId.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getExtId()));
        ordersTable.setItems(rows);

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVm, vm) -> {
            itemsList.getItems().clear();
            if (vm == null) return;
            if (orderIdField != null) orderIdField.setText(String.valueOf(vm.getOrderId()));
            Order o = vm.getOrder();
            if (o == null || o.getItems() == null) return;
            for (Item it : o.getItems()) {
                itemsList.getItems().add(it.getName() + " x" + it.getQuantity() + " @ " + it.getPrice());
            }
        });
    }

    public void setService(ServiceFacade service) {
        this.service = service;
        this.service.addListener(this::reloadTableOnFx);
        reloadTableOnFx();
    }

    @FXML private void onRefresh() {
        try { service.refreshFromDisk(); }
        catch (IOException e) { showAlert(Alert.AlertType.ERROR, "Refresh failed", e.getMessage()); }
    }

    @FXML private void onStart() {
        int id = parseOrderIdFromUi();
        if (id < 0) return;
        service.startOrder(id);
        info("Order " + id + " started.");
        reloadTableOnFx();
    }

    @FXML private void onComplete() {
        int id = parseOrderIdFromUi();
        if (id < 0) return;
        service.completeOrder(id);
        info("Order " + id + " completed.");
        reloadTableOnFx();
    }

    // Add button that opens up a new screen.  This one is different
    // Because it opens a new screen and loads xml - not just working the data
    @FXML
    private void onNewOrder() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Project2/newOrder.fxml")
        );
        Parent root = loader.load();

        // Get the Kotlin controller and pass ServiceFacade into it
        NewOrderController controller = loader.getController();
        controller.setService(service);

        Stage stage = new Stage();
        stage.setTitle("Add Order");
        stage.setScene(new Scene(root));

        // Set window size
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.setMinWidth(800);   // Minimum width
        stage.setMinHeight(500);  // Minimum height

        stage.initOwner(ordersTable.getScene().getWindow());
        stage.show();
    }

    @FXML private void onDelete() {
        int id = parseOrderIdFromUi();
        if (id < 0) return;
        boolean ok = service.deleteOrderIfNew(id);
        if (ok) info("Order " + id + " deleted.");
        else warn("Only NEW orders can be deleted.");
        reloadTableOnFx();
    }

    @FXML private void onDisplayIncomplete() {
        var list = service.listIncomplete();
        if (list.isEmpty()) {
            info("No incomplete orders.");
            return;
        }
        StringBuilder sb = new StringBuilder("Incomplete orders:\n");
        for (var w : list)
            sb.append("Order ID: " + w.getOrder().getOrderId()).append(" → ").append(w.getOrder().getOrderStatus()).append("\n");
        showAlert(Alert.AlertType.INFORMATION, "Incomplete Orders", sb.toString());
    }

    @FXML private void onPrintAll() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save all orders as JSON");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = chooser.showSaveDialog(ordersTable.getScene().getWindow());
        if (file == null) return;
        try {
            service.writeAllOrders(file);
            info("Wrote all orders to: " + file.getAbsolutePath());
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Write Failed", ex.getMessage());
        }
    }

    private void reloadTableOnFx() {
        Platform.runLater(() -> {
            rows.clear();
            for (Map.Entry<Integer, OrderWrapper> e : service.listAll().entrySet()) {
                Order o = e.getValue().getOrder();
                if (o != null) rows.add(new OrderVM(o));
            }
            statusLabel.setText("Loaded " + rows.size() + " order(s)");
        });
    }

    private int parseOrderIdFromUi() {
        String txt = orderIdField.getText();
        if (txt == null || txt.isBlank()) { warn("Enter Order ID first."); return -1; }
        try { return Integer.parseInt(txt.trim()); }
        catch (NumberFormatException e) { warn("Order ID must be numeric."); return -1; }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    private void info(String msg) { statusLabel.setText(msg); }
    private void warn(String msg) { statusLabel.setText(msg); }
}
