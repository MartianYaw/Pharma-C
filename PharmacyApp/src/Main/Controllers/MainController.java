package Main.Controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainPanel;


    public MainController(){
    }

    @FXML
    public void initialize() {

    }

    public void setPrimaryStage(Stage primaryStage) {
    }

    @FXML
    private void addDrug() {
        loadView("/main/resources/fxml/AddDrug.fxml");
    }

    @FXML
    private void viewReports() {
        loadView("/main/resources/fxml/ReportView.fxml");
    }

    @FXML
    private void viewAllDrugs() {
        loadView("/main/resources/fxml/ViewAllDrugs.fxml");
    }

    @FXML
    private void addSales() {
        loadView("/main/resources/fxml/AddSales.fxml");
    }

    @FXML
    private void viewSalesReport() {
        loadView("/main/resources/fxml/ViewSalesReport.fxml");
    }

    @FXML
    private void viewAllCustomers() {
        loadView("/main/resources/fxml/ViewAllCustomers.fxml");
    }

    @FXML
    private void linkSupplierToDrug() {
        loadView("/main/resources/fxml/LinkSupplierToDrug.fxml");
    }

    @FXML
    private void viewLinkedDrugAndSuppliers() {
        loadView("/main/resources/fxml/ViewSuppliers.fxml");
    }

    @FXML
    private void home(){
        loadView("/main/resources/fxml/home.fxml");
    }

    @FXML
    private void exitApplication() {
        // Exit the application
        System.exit(0);
    }

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

    private void showErrorAlert(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
