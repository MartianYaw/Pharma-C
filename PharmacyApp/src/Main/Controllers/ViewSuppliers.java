package Main.Controllers;

import Main.Models.LinkedData;
import Main.Models.Pharmacy;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;
import java.util.List;

/**
 * This JavaFX controller manages the user interface for viewing and searching suppliers linked to drugs.
 */
public class ViewSuppliers {

    @FXML
    private TableView<LinkedData> linkedTable;

    @FXML
    private TableColumn<LinkedData, String> drugCodeColumn;

    @FXML
    private TableColumn<LinkedData, String> drugNameColumn;

    @FXML
    private TableColumn<LinkedData, String> supplierNameColumn;

    @FXML
    private TableColumn<LinkedData, String> contactColumn;

    @FXML
    private TableColumn<LinkedData, String> locationColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helper;
    private final PharmacyManagement pharmacyManagement;
    private final Pharmacy pharmacy;

    /**
     * Constructor initializes helper, pharmacyManagement, and pharmacy objects.
     */
    public ViewSuppliers() {
        helper = new Helpers();
        pharmacyManagement = new PharmacyManagement();
        pharmacy = new Pharmacy();
    }

    /**
     * Initializes the controller, sets up the table columns, and loads all linked drugs and suppliers.
     */
    @FXML
    public void initialize() {
        // Set cell value factories to bind table columns to the corresponding properties in the LinkedData model.
        drugCodeColumn.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        drugNameColumn.setCellValueFactory(new PropertyValueFactory<>("drugName"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Calls viewLinkedDrugAndSuppliers to load all linked drugs and suppliers into the table.
        viewLinkedDrugAndSuppliers();

        // Sets up the search button action to call searchSuppliers when clicked.
        searchButton.setOnAction(event -> searchSuppliers());
    }

    /**
     * Loads all linked drugs and suppliers from the database and displays them in the table.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Background Task:
     * - Creates a Task to retrieve all linked drugs and suppliers from the database in a background thread.
     * - call Method: Calls pharmacy.getSuppliers() to retrieve the linked data.
     * - succeeded Method: On success, updates the table on the JavaFX Application Thread.
     * - failed Method: On failure, hides the loading indicator and shows an error alert.
     * - Start Task: Starts the task in a new thread.
     */
    @FXML
    private void viewLinkedDrugAndSuppliers() {
        loadingIndicator.setVisible(true);
        Task<List<LinkedData>> task = new Task<>() {
            @Override
            protected List<LinkedData> call() {
                return pharmacy.getSuppliers();
            }

            @Override
            protected void succeeded() {
                List<LinkedData> linkedData = getValue();
                linkedTable.getItems().setAll(linkedData);
                linkedTable.getStyleClass().add("table-view");
                Platform.runLater(() -> loadingIndicator.setVisible(false));
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                exception.printStackTrace();
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve linked drugs and suppliers.");
                });
            }
        };
        new Thread(task).start();
    }

    /**
     * Searches suppliers based on the input from the search field and displays the results in the table.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Search Input: Retrieves the input from the search field and trims it.
     * Background Task:
     * - Creates a Task to search suppliers based on the search input in a background thread.
     * - call Method: Calls pharmacyManagement.searchDrugSupplier(searchInput) to search for the suppliers.
     * - succeeded Method: On success, updates the table with the search results on the JavaFX Application Thread.
     * - failed Method: On failure, hides the loading indicator, shows an error alert, and reloads the linked drugs and suppliers.
     * - Start Task: Starts the task in a new thread.
     */
    @FXML
    private void searchSuppliers() {
        loadingIndicator.setVisible(true);
        String searchInput = searchField.getText().trim();
        if (!searchInput.isEmpty()) {
            Task<List<LinkedData>> task = new Task<>() {
                @Override
                protected List<LinkedData> call() {
                    return pharmacyManagement.searchDrugSupplier(searchInput);
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        List<LinkedData> found = getValue();
                        System.out.println(found);
                        if (found == null || found.isEmpty()) {
                            helper.showAlert(Alert.AlertType.INFORMATION, "No Results", "No suppliers found for " + searchInput);
                            viewLinkedDrugAndSuppliers();
                            loadingIndicator.setVisible(false);
                        } else {
                            linkedTable.getItems().setAll(found);
                            loadingIndicator.setVisible(false);
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to search suppliers.");
                        viewLinkedDrugAndSuppliers();
                        loadingIndicator.setVisible(false);
                    });
                }
            };
            new Thread(task).start();
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Search field cannot be empty.");
            viewLinkedDrugAndSuppliers();
            loadingIndicator.setVisible(false);
        }
    }
}
