package Main.Controllers;

import Main.Models.Pharmacy;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Main.Models.Purchase;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This JavaFX controller manages the user interface for viewing sales reports and searching purchase history.
 */
public class ViewSalesReport {
    @FXML
    private TableView<Purchase> salesTable;

    @FXML
    private TableColumn<Purchase, String> drugCodeColumn;

    @FXML
    private TableColumn<Purchase, String> quantityColumn;

    @FXML
    private TableColumn<Purchase, String> amountColumn;

    @FXML
    private TableColumn<Purchase, String> dateColumn;

    @FXML
    private TableColumn<Purchase, String> buyerColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helper;
    private final PharmacyManagement pharmacyManagement;
    private final Pharmacy pharmacy;

    /**
     * Constructor initializes helper, pharmacyManagement, and pharmacy objects.
     */
    public ViewSalesReport() {
        helper = new Helpers();
        pharmacyManagement = new PharmacyManagement();
        pharmacy = new Pharmacy();
    }

    /**
     * Initializes the controller, sets up the table columns, and loads all sales.
     */
    @FXML
    public void initialize(){
        drugCodeColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Drug Code")));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Quantity")));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Total Amount")));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("DateTime")));
        buyerColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Buyer")));

        viewAllSales();
    }

    /**
     * Loads all sales from the database and displays them in the table.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Background Task:
     * - Creates a Task to retrieve all sales from the database in a background thread.
     * - call Method: Calls pharmacy.getPurchases() to retrieve the sales data.
     * - succeeded Method: On success, updates the table on the JavaFX Application Thread.
     * - failed Method: On failure, hides the loading indicator and shows an error alert.
     * - Start Task: Starts the task in a new thread.
     */
    @FXML
    private void viewAllSales(){
        loadingIndicator.setVisible(true);
        Task<Map<LocalDateTime, Purchase>> task = new Task<>(){
            @Override
            protected Map<LocalDateTime, Purchase> call(){
                return pharmacy.getPurchases();
            }

            @Override
            protected void succeeded(){
                List<Purchase> purchases = new ArrayList<>(getValue().values());

                Platform.runLater(() -> {
                    salesTable.getItems().setAll(purchases);
                    loadingIndicator.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    helper.showError("Error", "Failed to retrieve sales from db.");
                    loadingIndicator.setVisible(false);
                });
            }
        };
        new Thread(task).start();
    }

    /**
     * Searches purchase history based on the input from the search field and displays the results in the table.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Search Input: Retrieves the input from the search field and trims it.
     * Background Task:
     * - Creates a Task to search purchase history based on the search input in a background thread.
     * - call Method: Calls pharmacyManagement.viewPurchaseHistory(searchInput) to search for the purchases.
     * - succeeded Method: On success, updates the table with the search results on the JavaFX Application Thread.
     * - failed Method: On failure, hides the loading indicator, shows an error alert, and reloads all sales.
     * - Start Task: Starts the task in a new thread.
     */
    @FXML
    private void viewPurchaseHistory(){
        loadingIndicator.setVisible(true);
        String searchInput = searchField.getText().trim();
        if (!searchInput.isEmpty()) {
            Task<Map<LocalDateTime, Purchase>> task = new Task<>(){
                @Override
                protected  Map<LocalDateTime, Purchase> call() throws Exception {
                    return pharmacyManagement.viewPurchaseHistory(searchInput);
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        Map<LocalDateTime, Purchase> purchases = getValue();
                        if (purchases == null || purchases.isEmpty()) {
                            helper.showAlert(Alert.AlertType.INFORMATION, "No Results", "No drugs found");
                            viewAllSales();
                            loadingIndicator.setVisible(false);
                        } else {
                            salesTable.getItems().setAll(purchases.values());
                            loadingIndicator.setVisible(false);
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable e = getException();
                        helper.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                        searchField.clear();
                        viewAllSales();
                        loadingIndicator.setVisible(false);
                    });
                }
            };
            new Thread(task).start();
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Search field cannot be empty.");
            viewAllSales();
            loadingIndicator.setVisible(false);
        }
    }
}
