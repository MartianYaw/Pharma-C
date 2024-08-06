package Main.Controllers;

import Main.Models.LinkedData;
import Main.Models.Pharmacy;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Main.Models.Drug;
import Main.Models.Supplier;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private final Helpers helper;
    private final PharmacyManagement pharmacyManagement;
    private final Pharmacy pharmacy;

    public ViewSuppliers() throws Exception {
        helper = new Helpers();
        pharmacyManagement = new PharmacyManagement();
        pharmacy = new Pharmacy();
    }

    @FXML
    public void initialize() {
        drugCodeColumn.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        drugNameColumn.setCellValueFactory(new PropertyValueFactory<>("drugName"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        viewLinkedDrugAndSuppliers();

        searchButton.setOnAction(event -> searchSuppliers());
    }

    @FXML
    private void viewLinkedDrugAndSuppliers() {
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
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                exception.printStackTrace();
                Platform.runLater(() -> helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve linked drugs and suppliers."));
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void searchSuppliers(){
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
                            helper.showAlert(Alert.AlertType.INFORMATION, "No Results", "No suppliers found in " + searchInput);
                            viewLinkedDrugAndSuppliers();
                        } else {
                            linkedTable.getItems().setAll(found);
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to search suppliers.");
                        viewLinkedDrugAndSuppliers();
                    });
                }
            };
            new Thread(task).start();
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Search field cannot be empty.");
            viewLinkedDrugAndSuppliers();
        }
    }
}
