package Project2

import javafx.fxml.FXML
import javafx.scene.control.Button

class NewOrderController {

    @FXML lateinit var itemA: Button
    @FXML lateinit var itemB: Button
    @FXML lateinit var itemC: Button
    @FXML lateinit var itemD: Button
    @FXML lateinit var itemE: Button
    @FXML lateinit var itemF: Button

    @FXML
    fun initialize() {
        // later: hook up real actions
        // for now this is just layout
    }
}
