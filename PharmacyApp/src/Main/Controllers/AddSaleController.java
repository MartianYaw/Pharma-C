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

    public AddSaleController() {
        helpers = new Helpers();
        pharmacyManagement = new PharmacyManagement();
    }

    @FXML
    public void initialize() {
        drugCodeCol.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        drugCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        drugCodeCol.setOnEditCommit(event -> event.getRowValue().setDrugCode(event.getNewValue()));

        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> event.getRowValue().setQuantity(event.getNewValue()));

        addRowButton.setOnAction(event -> salesTable.getItems().add(new Purchase()));

        addButtonToTable();
    }

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
    @FXML
    public void handleSales() {
        loadingIndicator.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String buyer = buyerField.getText();
                String contactInfo = contactField.getText();
                List<Purchase> purchases = salesTable.getItems();
                if(buyer.isEmpty() || contactInfo.isEmpty()){
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
