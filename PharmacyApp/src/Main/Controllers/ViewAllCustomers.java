package Main.Controllers;

import Main.Models.Customers;
import Main.Models.Pharmacy;
import Main.Services.Helpers;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * This JavaFX controller manages the user interface for viewing all customers in the pharmacy database.
 */
public class ViewAllCustomers {

    @FXML
    private TableView<Customers> customerTable;

    @FXML
    private TableColumn<Customers, String> nameColumn;

    @FXML
    private TableColumn<Customers, String> phoneColumn;

    @FXML
    private TableColumn<Customers, String> purchasesColumn;

    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helper;
    private final Pharmacy pharmacy;

    /**
     * Constructor initializes helper and pharmacy objects.
     */
    public ViewAllCustomers() {
        helper = new Helpers();
        pharmacy = new Pharmacy();
    }

    /**
     * Initializes the controller, sets up the table columns, and loads all customers.
     */
    @FXML
    public void initialize() {
        // Set cell value factories to bind table columns to the corresponding properties in the Customers model.
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Buyer")));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Phone Number")));
        purchasesColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Number Of Purchases")));

        // Calls viewAllCustomers to load all customers into the table.
        viewAllCustomers();
    }

    /**
     * Loads all customers from the database and displays them in the table.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Background Task:
     * - Creates a Task to retrieve all customers from the database in a background thread.
     * - call Method: Calls pharmacy.getCustomers() to retrieve the customers.
     * - succeeded Method: On success, updates the table on the JavaFX Application Thread.
     * - failed Method: On failure, hides the loading indicator and shows an error alert.
     * - Start Task: Starts the task in a new thread.
     */
    @FXML
    private void viewAllCustomers() {
        loadingIndicator.setVisible(true);
        Task<List<Customers>> task = new Task<>() {
            @Override
            protected List<Customers> call() {
                return pharmacy.getCustomers();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    customerTable.getItems().setAll(getValue());
                    loadingIndicator.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    helper.showError("Error", "Failed to retrieve customers from db.");
                    loadingIndicator.setVisible(false);
                });
            }
        };
        new Thread(task).start();
    }
}
