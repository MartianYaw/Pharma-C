package Main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for managing the main application interface.
 * Handles navigation between different views and application exit.
 */
public class MainController {

    @FXML
    private BorderPane mainPanel; // Main panel where different views are loaded

    /**
     * Initializes the controller. This method is called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
    }

    /**
     * Sets the primary stage of the application.
     *
     * @param primaryStage The primary stage for the application.
     */
    public void setPrimaryStage(Stage primaryStage) {
        // Set the primary stage if needed for further use
    }

    /**
     * Loads the view for adding a new drug.
     */
    @FXML
    private void addDrug() {
        loadView("/main/resources/fxml/AddDrug.fxml");
    }

    /**
     * Loads the view for generating and viewing reports.
     */
    @FXML
    private void viewReports() {
        loadView("/main/resources/fxml/ReportView.fxml");
    }

    /**
     * Loads the view for displaying all drugs.
     */
    @FXML
    private void viewAllDrugs() {
        loadView("/main/resources/fxml/ViewAllDrugs.fxml");
    }

    /**
     * Loads the view for adding new sales.
     */
    @FXML
    private void addSales() {
        loadView("/main/resources/fxml/AddSales.fxml");
    }

    /**
     * Loads the view for displaying the sales report.
     */
    @FXML
    private void viewSalesReport() {
        loadView("/main/resources/fxml/ViewSalesReport.fxml");
    }

    /**
     * Loads the view for displaying all customers.
     */
    @FXML
    private void viewAllCustomers() {
        loadView("/main/resources/fxml/ViewAllCustomers.fxml");
    }

    /**
     * Loads the view for linking a supplier to a drug.
     */
    @FXML
    private void linkSupplierToDrug() {
        loadView("/main/resources/fxml/LinkSupplierToDrug.fxml");
    }

    /**
     * Loads the view for displaying linked drugs and suppliers.
     */
    @FXML
    private void viewLinkedDrugAndSuppliers() {
        loadView("/main/resources/fxml/ViewSuppliers.fxml");
    }

    /**
     * Loads the home view.
     */
    @FXML
    private void home() {
        loadView("/main/resources/fxml/home.fxml");
    }

    /**
     * Exits the application.
     */
    @FXML
    private void exitApplication() {
        // Exit the application
        System.exit(0);
    }

    /**
     * Loads a view from the specified FXML path and sets it to the center of the main panel.
     *
     * @param fxmlPath The path to the FXML file.
     */
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPanel.setCenter(view);
        } catch (IOException e) {
            showErrorAlert("Failed to load the view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Displays an error alert with the specified content.
     *
     * @param content The content to be displayed in the error alert.
     */
    private void showErrorAlert(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
