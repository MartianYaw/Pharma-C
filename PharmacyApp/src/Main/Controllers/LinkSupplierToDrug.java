package Main.Controllers;

import Main.Models.Supplier;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * This JavaFX controller manages the user interface for linking a supplier to a drug in the pharmacy database.
 */
public class LinkSupplierToDrug {
    @FXML
    private TextField drugCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField contactField;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helpers;
    private final PharmacyManagement pharmacyManagement;

    /**
     * Constructor initializes helpers and pharmacy management objects.
     */
    public LinkSupplierToDrug() {
        helpers = new Helpers();
        pharmacyManagement = new PharmacyManagement();
    }

    /**
     * Handles the linking of a supplier to a drug when the link button is clicked.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Input Validation:
     *   - Checks if all input fields are filled.
     * Background Task:
     *   - Creates a Task to link the supplier to the drug in a background thread.
     *   - call Method: Retrieves input values, validates them, creates a Supplier object, and links it to the drug.
     *   - succeeded Method: On success, clears the input fields and hides the loading indicator.
     *   - failed Method: On failure, prints the stack trace, hides the loading indicator, and shows an error alert.
     * Start Task: Starts the task in a new thread.
     */
    @FXML
    private void handleLink() {
        loadingIndicator.setVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String drugCode = drugCodeField.getText();
                String name = nameField.getText();
                String location = locationField.getText();
                String contact = contactField.getText();

                if (drugCode.isEmpty() || name.isEmpty() || location.isEmpty() || contact.isEmpty()) {
                    loadingIndicator.setVisible(false);
                    throw new Exception("All fields are required");
                }
                Supplier supplier = new Supplier(name, location, contact);
                pharmacyManagement.linkSupplierToDrug(drugCode, supplier);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    helpers.showAlert(Alert.AlertType.INFORMATION, "Success", "Drug Linked To Supplier");
                    drugCodeField.clear();
                    nameField.clear();
                    locationField.clear();
                    contactField.clear();
                    loadingIndicator.setVisible(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable throwable = getException();
                    throwable.printStackTrace();
                    loadingIndicator.setVisible(false);
                    helpers.showAlert(Alert.AlertType.ERROR, "Error", "Could not process linking: " + throwable.getMessage());
                });
            }
        };
        new Thread(task).start();
    }
}
