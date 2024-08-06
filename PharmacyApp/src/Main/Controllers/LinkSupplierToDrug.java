package Main.Controllers;

import Main.Models.Supplier;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LinkSupplierToDrug {
    @FXML
    private TextField drugCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField contactField;

    private final Helpers helpers;
    private final PharmacyManagement pharmacyManagement;

    public LinkSupplierToDrug() {
        helpers = new Helpers();
        pharmacyManagement = new PharmacyManagement();
    }

    @FXML
    private void handleLink() {
        Task<Void> task = new Task<>(){
            @Override
            protected Void call() throws Exception{
                String drugCode = drugCodeField.getText();
                String name = nameField.getText();
                String location = locationField.getText();
                String contact = contactField.getText();

                if(drugCode.isEmpty() || name.isEmpty() || location.isEmpty() || contact.isEmpty()){
                    throw new Exception("All fields are required");
                }

                Supplier supplier = new Supplier(name, location, contact);

                pharmacyManagement.linkSupplierToDrug(drugCode, supplier);
                return null;
            }

            @Override
            protected void succeeded(){
                Platform.runLater(() -> {
                    helpers.showAlert(Alert.AlertType.INFORMATION, "Success", "Drug Linked To Supplier");
                    drugCodeField.clear();
                    nameField.clear();
                    locationField.clear();
                    contactField.clear();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable throwable = getException();
                    throwable.printStackTrace();
                    helpers.showAlert(Alert.AlertType.ERROR, "Error", "Could not process purchases: " + throwable.getMessage());
                });
            }
        };
        new Thread(task).start();
    }
}
