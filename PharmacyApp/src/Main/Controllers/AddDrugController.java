package Main.Controllers;

import Main.Services.PharmacyManagement;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Main.Models.Drug;
import Main.Services.Helpers;

/**
 * This JavaFX controller manages the user interface for adding new drugs to the pharmacy database.
 */
public class AddDrugController {
    @FXML
    private TextField drugCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockQuantityField;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helpers;
    private final PharmacyManagement pharmacyManagement;

    /**
     * Constructor initializes helpers and pharmacy management objects.
     */
    public AddDrugController() {
        helpers = new Helpers();
        pharmacyManagement = new PharmacyManagement();
    }

    /**
     * Handles the addition of a new drug to the pharmacy database.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Input Validation:
     *   - Checks if all input fields are filled.
     *   - Validates the format of price and stock quantity fields.
     * Background Task:
     *   - Creates a Task to add the drug to the database in a background thread.
     *   - call Method: Retrieves input values, validates them, creates a Drug object, and adds it to the database.
     *   - succeeded Method: On success, clears the input fields, hides the loading indicator, and shows a success alert.
     *   - failed Method: On failure, prints the stack trace, hides the loading indicator, and shows an error alert.
     * Start Task: Starts the task in a new thread.
     */
    @FXML
    private void handleAddDrug() {
        loadingIndicator.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String drugCode = drugCodeField.getText();
                String name = nameField.getText();
                String category = categoryField.getText();
                String priceText = priceField.getText();
                String stockText = stockQuantityField.getText();

                if (drugCode.isEmpty() || name.isEmpty() || category.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
                    throw new Exception("All drug credentials must be filled");
                }

                try {
                    double price = Double.parseDouble(priceField.getText());
                    int stockQuantity = Integer.parseInt(stockQuantityField.getText());
                    Drug drug = new Drug(drugCode, name, category, price, stockQuantity);
                    pharmacyManagement.addDrug(drug);
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid number format");
                }

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    helpers.showAlert(Alert.AlertType.INFORMATION, "Success", "Drug added successfully");
                    drugCodeField.clear();
                    nameField.clear();
                    categoryField.clear();
                    priceField.clear();
                    stockQuantityField.clear();
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
