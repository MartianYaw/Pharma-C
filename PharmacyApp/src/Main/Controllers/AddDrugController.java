package Main.Controllers;

import Main.Services.PharmacyManagement;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Main.Models.Drug;
import Main.Services.Helpers;
import javafx.scene.layout.VBox;

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
    private VBox addDrugForm;

    private final Helpers helpers;
    private final PharmacyManagement pharmacyManagement;

    public AddDrugController() {
        helpers = new Helpers();
        pharmacyManagement = new PharmacyManagement();
    }

    @FXML
    private void handleAddDrug() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception{
                String drugCode = drugCodeField.getText();
                String name = nameField.getText();
                String category = categoryField.getText();
                String priceText = priceField.getText();
                String stockText = stockQuantityField.getText();
                if(drugCode.isEmpty() || name.isEmpty() || category.isEmpty() || priceText.isEmpty() || stockText.isEmpty()){
                    throw new Exception("All drug credentials must be filled");
                }
                try {
                    double price = Double.parseDouble(priceField.getText());
                    int stockQuantity = Integer.parseInt(stockQuantityField.getText());
                    Drug drug = new Drug(drugCode, name, category, price, stockQuantity);
                    pharmacyManagement.addDrug(drug);
                }catch(NumberFormatException e){
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
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable throwable = getException();
                    throwable.printStackTrace();
                    helpers.showAlert(Alert.AlertType.ERROR, "Error", throwable.getMessage());
                });
            }
        };
        new Thread(task).start();
    }
}