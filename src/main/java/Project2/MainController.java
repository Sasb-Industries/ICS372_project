package Project2;

import Project1.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Map;

public class MainController {

    // FXML-injected controls
    @FXML private TableView<OrderVM> ordersTable;
    @FXML private TableColumn<OrderVM, Number> colId;
    @FXML private TableColumn<OrderVM, String> colType;
    @FXML private TableColumn<OrderVM, String> colStatus;
    @FXML private TableColumn<OrderVM, Number> colTotal;

    @FXML private ListView<String> itemsList;
    @FXML private Label statusLabel;

    // Domain facade
    private ServiceFacade service;

    // UI model
    private final ObservableList<OrderVM> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        ordersTable.setItems(rows);
        colId.setCellValueFactory(data -> data.getValue().orderIdProperty());
        colType.setCellValueFactory(data -> data.getValue().typeProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        colTotal.setCellValueFactory(data -> data.getValue().totalProperty());

        // When a row is selected, list its items at the right
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVm, vm) -> {
            itemsList.getItems().clear();
            if (vm == null) return;
            Order o = vm.getOrder();
            if (o == null || o.getItems() == null) return;
            for (Item it : o.getItems()) {
                itemsList.getItems().add(it.getName() + " x " + it.getQuantity() + " @ " + it.getPrice());
            }
        });
    }

    // Called by uiMain after FXML is loaded
    public void setService(ServiceFacade service) {
        this.service = service;
        this.service.addListener(this::reloadTableOnFx);
        reloadTableOnFx(); // initial paint
    }

    @FXML
    private void onRefresh() throws IOException {
        if (service != null) service.refreshFromDisk();
    }

    /** Rebuild table rows from the service cache on the FX thread. */
    private void reloadTableOnFx() {
        Platform.runLater(() -> {
            rows.clear();

            if (service == null) return;
            for (Map.Entry<Integer, OrderWrapper> e : service.listAll().entrySet()) {
                OrderWrapper ow = e.getValue();
                Order o = ow.getOrder();
                if (o == null) continue;

                rows.add(new OrderVM(o));
            }
            statusLabel.setText("Loaded " + rows.size() + " order(s)");
        });
    }
}
