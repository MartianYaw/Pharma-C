package Main.Controllers;

import Main.Models.Purchase;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This JavaFX controller manages the user interface for adding new sales to the pharmacy database.
 */
public class AddSaleController {
    @FXML
    private TableView<Purchase> salesTable;

    @FXML
    private TableColumn<Purchase, String> drugCodeCol;

    @FXML
    private TableColumn<Purchase, Integer> quantityCol;

    @FXML
    private TableColumn<Purchase, Void> actionCol;

    @FXML
    private TextField buyerField;
    @FXML
    private TextField contactField;
    @FXML
    private Button addRowButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helpers;
    private final PharmacyManagement pharmacyManagement;

    /**
     * Constructor initializes helpers and pharmacy management objects.
     */
    public AddSaleController() {
        helpers = new Helpers();
        pharmacyManagement = new PharmacyManagement();
    }

    /**
     * Initializes the controller, sets up the table columns, and adds functionality to the Add Row button.
     * Method Annotation: Indicates this method is called from FXML.
     */
    @FXML
    public void initialize() {
        // Binds the drug code column to the drugCode property of the Purchase model and makes it editable.
        drugCodeCol.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        drugCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        //makes the cell editable and sets the row value to the edit value
        drugCodeCol.setOnEditCommit(event -> event.getRowValue().setDrugCode(event.getNewValue()));

        // Binds the quantity column to the quantity property of the Purchase model and makes it editable.
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        //makes the cell editable and sets the row value to the edit value
        quantityCol.setOnEditCommit(event -> event.getRowValue().setQuantity(event.getNewValue()));

        // Adds a new row to the sales table when the Add Row button is clicked.
        addRowButton.setOnAction(event -> salesTable.getItems().add(new Purchase()));

        // Adds a delete button to each row in the action column.
        addButtonToTable();
    }

    /**
     * Adds a delete button to each row in the action column, allowing users to remove purchases from the table.
     */
    private void addButtonToTable() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Purchase purchase = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(purchase);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    /**
     * Handles the processing of sales and adding them to the pharmacy database.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Input Validation:
     *   - Checks if all input fields are filled.
     *   - Validates if there are any purchases to process.
     * Background Task:
     *   - Creates a Task to add the sales to the database in a background thread.
     *   - call Method: Retrieves input values, validates them, updates the Purchase objects, and adds them to the database.
     *   - succeeded Method: On success, clears the input fields, clears the table, and hides the loading indicator.
     *   - failed Method: On failure, prints the stack trace, hides the loading indicator, and shows an error alert.
     * Start Task: Starts the task in a new thread.
     */
    @FXML
    public void handleSales() {
        loadingIndicator.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String buyer = buyerField.getText();
                String contactInfo = contactField.getText();
                List<Purchase> purchases = salesTable.getItems();

                if (buyer.isEmpty() || contactInfo.isEmpty()) {
                    throw new Exception("All customer credentials must be filled");
                }

                if (purchases.isEmpty()) {
                    throw new Exception("No purchases provided");
                }

                for (Purchase purchase : purchases) {
                    purchase.setBuyerName(buyer);
                    purchase.setContactInfo(contactInfo);
                    purchase.setDateTime(LocalDateTime.now());
                }

                pharmacyManagement.addSales(purchases);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    helpers.showAlert(Alert.AlertType.INFORMATION, "Success", "Sales added successfully");
                    buyerField.clear();
                    contactField.clear();
                    salesTable.getItems().clear();
                    loadingIndicator.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable throwable = getException();
                    throwable.printStackTrace();
                    loadingIndicator.setVisible(false);
                    helpers.showAlert(Alert.AlertType.ERROR, "Error", throwable.getMessage());
                });
            }
        };
        new Thread(task).start();
    }
}
