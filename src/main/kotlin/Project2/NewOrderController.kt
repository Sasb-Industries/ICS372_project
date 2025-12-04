package Project2

import Project1.Item
import Project1.Order
import Project1.OrderWrapper
import Project1.ServiceFacade
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage

data class OrderRow(
    var name: String = "",
    var quantity: Int = 0,
    var unitPrice: Double = 0.0
)

class NewOrderController {

    // Service facade to submit our order
    private lateinit var service: ServiceFacade

    @FXML lateinit var itemA: Button
    @FXML lateinit var itemB: Button
    @FXML lateinit var itemC: Button
    @FXML lateinit var itemD: Button
    @FXML lateinit var itemE: Button
    @FXML lateinit var itemF: Button

    // Table + columns
    @FXML lateinit var orderTable: TableView<OrderRow>
    @FXML lateinit var colName: TableColumn<OrderRow, String>
    @FXML lateinit var colQty: TableColumn<OrderRow, Int>
    @FXML lateinit var colPrice: TableColumn<OrderRow, Double>

    @FXML lateinit var totalLabel: Label

    private val rows = FXCollections.observableArrayList<OrderRow>()

    fun setService(service: ServiceFacade) {
        this.service = service
    }

    @FXML
    fun initialize() {

        // Table setup
        orderTable.items = rows

        colName.cellValueFactory =
            PropertyValueFactory<OrderRow, String>("name")

        colQty.cellValueFactory =
            PropertyValueFactory<OrderRow, Int>("quantity")

        colPrice.cellValueFactory =
            PropertyValueFactory<OrderRow, Double>("unitPrice")

        itemA.setOnAction {
            println("Hamburger Selected !")
            addItem("Hamburger", 9.99)}

        itemB.setOnAction {
            println("French Fries Selected !")
            addItem("French Fries", 3.49)}

        itemC.setOnAction {
            println("Caesar Salad Selected !")
            addItem("Caesar Salad", 7.25)}

        itemD.setOnAction {
            println("Fountain Drink Selected !")
            addItem("Fountain Drink", 1.99)}

        itemE.setOnAction {
            println("Ice Cream Sundae Selected !")
            addItem("Ice Cream Sundae", 4.50)}

        itemF.setOnAction {
            println("Chicken Sandwich Selected !")
            addItem("Chicken Sandwich", 8.75)}

        updateTotal()
    }

    private fun addItem(name: String, unitPrice: Double) {
        // If row already exists, increment quantity
        val existing = rows.firstOrNull {
            it.name == name && it.unitPrice == unitPrice }
        if (existing != null) {
            existing.quantity += 1
            orderTable.refresh()
        } else {
            rows.add(OrderRow(name, 1, unitPrice))
        }
        updateTotal()
    }

    private fun updateTotal() {
        val total = rows.sumOf { it.unitPrice * it.quantity }
        totalLabel.text = String.format("$%.2f", total)
    }

    // Submitting the order to the service facade
    @FXML
    fun onSubmit() {
        // If service not wired or no items, just close
        if (!::service.isInitialized || rows.isEmpty()) {
            val stage = orderTable.scene.window as Stage
            stage.close()
            return
        }

        // Convert to domain items
        val items = rows.map { row ->
            Item().apply {
                name = row.name
                quantity = row.quantity
                price = row.unitPrice
            }
        }

        // Build the Order; Restaurant/ServiceFacade will assign orderId
        val order = Order().apply {
            setItems(items)
            setType("Walk-in")                    // or "Walk-in"
            setOrderDate(System.currentTimeMillis())
            setSource("In-House")                      // mark as created from UI
        }

        val wrapper = OrderWrapper().apply {
            setOrder(order)
        }

        // Reuse existing pipeline for new orders
        service.addParsedOrder(wrapper)

        val stage = orderTable.scene.window as Stage
        stage.close()
    }
}
