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

/**
 * Main class for the Pharmacy Management System application.
 * This class initializes and launches the JavaFX application.
 */
public class Main extends Application {

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        Helpers helper = new Helpers();
        try {
            // Loads the main FXML layout for the application
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/main.fxml"));

            // Parses the FXML file and generate the scene graph
            BorderPane root = loader.load();

            // Creates a new scene with the loaded layout
            Scene scene = new Scene(root);

            // Applies the external CSS stylesheet to the scene
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());

            // Gets the controller associated with the main layout from the FXMLLoader
            MainController controller = loader.getController();

            // Passes the primary stage to the controller
            controller.setPrimaryStage(primaryStage);

            // Sets the title of the primary stage (main application window)
            primaryStage.setTitle("Pharmacy Management System");

            // Sets the minimum width and height of the primary stage
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(700);

            // Sets the scene for the primary stage
            primaryStage.setScene(scene);

            // Shows the primary stage, making it visible to the user
            primaryStage.show();
        } catch (IOException e) {
            // Prints the stack trace for the exception
            e.printStackTrace();

            // Shows an alert to the user indicating that loading the application failed
            helper.showAlert(Alert.AlertType.ERROR, "Failed to load the application", "Error", e.getMessage());
        } catch (NullPointerException e) {
            // Prints the stack trace for the exception
            e.printStackTrace();

            // Shows an alert to the user indicating that a resource was not found
            helper.showAlert(Alert.AlertType.ERROR, "Resource not found", "Error", e.getMessage());
        }
    }

    /**
     * The main method is ignored in correctly deployed JavaFX applications.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
