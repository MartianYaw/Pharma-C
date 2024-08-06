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

    public ViewAllCustomers() {
        helper = new Helpers();
        pharmacy = new Pharmacy();
    }

    @FXML
    public void initialize(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Buyer")));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Phone Number")));
        purchasesColumn.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName("Number Of Purchases")));
        viewAllCustomers();
    }

    @FXML
    private void viewAllCustomers(){
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
