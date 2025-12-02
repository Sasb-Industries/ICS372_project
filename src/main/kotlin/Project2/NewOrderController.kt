package Project2

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView

class NewOrderController {

    @FXML lateinit var itemA: Button
    @FXML lateinit var itemB: Button
    @FXML lateinit var itemC: Button
    @FXML lateinit var itemD: Button
    @FXML lateinit var itemE: Button
    @FXML lateinit var itemF: Button

    // List for running order tab
    @FXML lateinit var orderList: ListView<String>

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
}
