package Main;

import Main.Controllers.MainController;
import Main.Services.Helpers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage){
        Helpers helper = new Helpers();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/main.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());

            MainController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setTitle("Pharmacy Management System");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(700);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            helper.showAlert(Alert.AlertType.ERROR,"Failed to load the application","Error",  e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            helper.showAlert(Alert.AlertType.ERROR,"Resource not found","Error",  e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
