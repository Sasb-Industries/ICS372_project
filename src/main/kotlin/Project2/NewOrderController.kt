package Project2

import Project1.ServiceFacade
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.stage.Stage

class NewOrderController {

    // Service facade to submit our order
    private lateinit var service: ServiceFacade

    @FXML lateinit var itemA: Button
    @FXML lateinit var itemB: Button
    @FXML lateinit var itemC: Button
    @FXML lateinit var itemD: Button
    @FXML lateinit var itemE: Button
    @FXML lateinit var itemF: Button

    // List for running order tab
    @FXML lateinit var orderList: ListView<String>

    fun setService(service: ServiceFacade) {
        this.service = service
    }

    @FXML
    fun initialize() {

        itemA.setOnAction {
            println("Hamburger Selected !")
            orderList.items.add("Hamburger")}

        itemB.setOnAction {
            println("French Fries Selected !")
            orderList.items.add("French Fries")}

        itemC.setOnAction {
            println("Caesar Salad Selected !")
            orderList.items.add("Caesar Salad")}

        itemD.setOnAction {
            println("Fountain Drink Selected !")
            orderList.items.add("Fountain Drink")}

        itemE.setOnAction {
            println("Ice Cream Sundae Selected !")
            orderList.items.add("Ice Cream Sundae")}

        itemF.setOnAction {
            println("Chicken Sandwich Selected !")
            orderList.items.add("Chicken Sandwich")}
    }

    // Submitting the order to the service facade
    @FXML
    fun onSubmit() {
        val items = orderList.items.toList()
        println("Submitting order")

        // for now do nothing, what I had wasn't working

        // Close the window
        val stage = orderList.scene.window as Stage
        stage.close()
    }
}
