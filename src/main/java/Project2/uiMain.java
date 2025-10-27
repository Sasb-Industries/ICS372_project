package Project2;

import Project1.ServiceFacade;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class uiMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Domain: point to the root "Input Orders" folder next to the project root
        Path inputDir = Paths.get("Input Orders");
        Files.createDirectories(inputDir);
        ServiceFacade service = new ServiceFacade(inputDir);

        // Load FXML and inject the service
        var url = uiMain.class.getResource("/Project2/main.fxml");
        System.out.println("FXML URL = " + url); // must NOT be null
        FXMLLoader fx = new FXMLLoader(url);
        Parent root = fx.load();
        MainController controller = fx.getController();
        controller.setService(service);

        stage.setScene(new Scene(root));
        stage.setTitle("Orders Viewer");
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
