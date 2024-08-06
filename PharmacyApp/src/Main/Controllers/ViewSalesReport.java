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
    private Button searchButton;

    private final Helpers helper;
    private final PharmacyManagement pharmacyManagement;
    private final Pharmacy pharmacy;

    public ViewSalesReport() {
        helper = new Helpers();
        pharmacyManagement = new PharmacyManagement();
        pharmacy = new Pharmacy();
    }

    @FXML
    public void initialize(){
        drugCodeColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Drug Code")));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Quantity")));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Total Amount")));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("DateTime")));
        buyerColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Buyer")));

        viewAllSales();
    }

    @FXML
    private void viewAllSales(){
        Task<Map<LocalDateTime, Purchase>> task = new Task<>(){
           @Override
           protected Map<LocalDateTime, Purchase> call(){
               return pharmacy.getPurchases();
           }

           @Override
            protected void succeeded(){
               List<Purchase> purchases = new ArrayList<>(getValue().values());
               Platform.runLater(() -> salesTable.getItems().setAll(purchases));
           }
            @Override
            protected void failed() {
                Platform.runLater(() -> helper.showError("Error", "Failed to retrieve sales from db."));
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void viewPurchaseHistory(){
        String searchInput = searchField.getText().trim();
        if(!searchInput.isEmpty()){
            Task<Map<LocalDateTime, Purchase>> task = new Task<>(){
                @Override
                protected  Map<LocalDateTime, Purchase> call() throws Exception{
                    return pharmacyManagement.viewPurchaseHistory(searchInput);
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        Map<LocalDateTime, Purchase> purchases = getValue();
                        if (purchases == null || purchases.isEmpty()) {
                            helper.showAlert(Alert.AlertType.INFORMATION, "No Results", "No drugs found");
                            viewAllSales();
                        } else {
                            salesTable.getItems().setAll(purchases.values());
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
                    });
                }
            };
            new Thread(task).start();

        }else{
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Search field cannot be empty.");
            viewAllSales();
        }

    }
}
