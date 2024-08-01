package Main;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private final PharmacyManagement pharmacyManagement = new PharmacyManagement();
    private final Helpers helper = new Helpers();
    private Stage primaryStage;
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    @FXML
    private BorderPane mainPanel;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void addDrug() {
        // Creates Text Fields
        TextField drugCodeField = new TextField();
        TextField nameField = new TextField();
        TextField categoryField = new TextField();
        TextField priceField = new TextField();
        TextField stockQuantityField = new TextField();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Drug Code:"), 0, 0);
        grid.add(drugCodeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Stock Quantity:"), 0, 4);
        grid.add(stockQuantityField, 1, 4);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Drug");
        dialog.getDialogPane().setContent(grid);
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Adding drug, please wait...");
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            String drugCode = drugCodeField.getText();
                            String name = nameField.getText();
                            String category = categoryField.getText();
                            double price = Double.parseDouble(priceField.getText());
                            int stockQuantity = Integer.parseInt(stockQuantityField.getText());

                            Platform.runLater(loadingDialog::showLoading);
                            // Create Drug object
                            Drug drug = new Drug(drugCode, name, category, price, stockQuantity);

                            // Call method to add drug
                            pharmacyManagement.addDrug(drug);
                        } catch (NumberFormatException e) {
                            Platform.runLater(() -> helper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format."));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add drug to db."));
                        } finally {
                            Platform.runLater(loadingDialog::hideLoading);
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> helper.showAlert(Alert.AlertType.INFORMATION, "Success", "Drug added successfully"));
                    }

                    @Override
                    protected void failed() {
                        Platform.runLater(() -> helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to add drug to db."));
                    }
                };
                new Thread(task).start();
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void searchDrugByCode() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Drug By Code");
        dialog.setHeaderText("Enter drug code:");

        dialog.showAndWait().ifPresent(drugCode -> {
            Drug drug = pharmacyManagement.searchDrugByCode(drugCode);

            helper.ifPresent(drug);
        });
    }
    @FXML
    private void searchDrugByName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Drug By Name");
        dialog.setHeaderText("Enter drug name:");

        dialog.showAndWait().ifPresent(drugName -> {
            Drug drug = pharmacyManagement.searchDrugByName(drugName);

            helper.ifPresent(drug);
        });
    }

    @FXML
    private void searchSuppliers() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Suppliers By Location");
        dialog.setHeaderText("Enter Location:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Retrieving suppliers, please wait...");
            Task<List<Supplier>> task = new Task<>() {
                @Override
                protected List<Supplier> call() {
                    Platform.runLater(loadingDialog::showLoading);
                    return pharmacyManagement.searchDrugSupplier(result.get());
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(loadingDialog::hideLoading);

                    List<Supplier> suppliers = getValue();
                    String[] columnNames = {"Name", "Location", "Phone Number"};
                    TableView<Supplier> table = new TableView<>();
                    table.setPrefSize(600, 400);
                    table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
                    for (String columnName : columnNames) {
                        TableColumn<Supplier, String> column = new TableColumn<>(columnName);
                        column.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName(columnName)));
                        column.setMinWidth(120);
                        column.setStyle("-fx-alignment: CENTER;");
                        table.getColumns().add(column);
                    }

                    table.getItems().addAll(suppliers);
                    table.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
                    table.getStyleClass().add("table-view");
                    helper.showAlert(Alert.AlertType.INFORMATION, "Suppliers from " + result.get(), table);
                }

                @Override
                protected void failed() {
                    Platform.runLater(loadingDialog::hideLoading);
                    helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve suppliers.");
                }
            };

            new Thread(task).start();
        }
    }
    @FXML
    private void viewAllDrugs() {
        LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Retrieving drugs, please wait...");
        Task<List<Drug>> task = new Task<>() {
            @Override
            protected List<Drug> call() {
                Platform.runLater(loadingDialog::showLoading);
                return pharmacyManagement.viewAllDrugs();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(loadingDialog::hideLoading);
                List<Drug> drugs = getValue();
                String[] columnNames = {"Drug Code", "Name", "Category", "Price", "Stock Quantity"};

                TableView<Drug> table = new TableView<>();
                table.setPrefSize(600, 400);
                table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

                for (String columnName : columnNames) {
                    TableColumn<Drug, String> column = new TableColumn<>(columnName);
                    column.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName(columnName)));
                    column.setMinWidth(120);  // Set column width
                    column.setStyle("-fx-alignment: CENTER;"); // Center-align cell content
                    table.getColumns().add(column);
                }

                Iterator<Drug> iterator = drugs.iterator();
                while (iterator.hasNext()) {
                    table.getItems().add(iterator.next());
                }
                table.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
                table.getStyleClass().add("table-view");

                helper.showAlert(Alert.AlertType.INFORMATION, "All Drugs", table);
            }
        };
        new Thread(task).start();
    }
    @FXML
    private void linkSupplierToDrug() {
        // Create Text Fields
        TextField supplierNameField = new TextField();
        TextField drugCodeField = new TextField();
        TextField locationField = new TextField();
        TextField contactInfoField = new TextField();

        // Create a GridPane that contains each text field with its label
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Drug Code:"), 0, 0);
        grid.add(drugCodeField, 1, 0);
        grid.add(new Label("Supplier Name:"), 0, 1);
        grid.add(supplierNameField, 1, 1);
        grid.add(new Label("Contact Info:"), 0, 2);
        grid.add(contactInfoField, 1, 2);
        grid.add(new Label("Location:"), 0, 3);
        grid.add(locationField, 1, 3);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Link Supplier to Drug");
        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Set up result converter to handle button actions
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                // Show loading dialog
                LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Linking supplier, please wait...");

                // Retrieve field values
                String supplierName = supplierNameField.getText();
                String drugCode = drugCodeField.getText();
                String location = locationField.getText();
                String contactInfo = contactInfoField.getText();

                // Perform linking in a background task
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        Platform.runLater(loadingDialog::showLoading);
                        // Create Supplier object
                        Supplier supplier = new Supplier(supplierName, location, contactInfo);
                        // Call method to link supplier to drug
                        pharmacyManagement.linkSupplierToDrug(drugCode, supplier);
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        // Hide loading dialog on task success
                        Platform.runLater(loadingDialog::hideLoading);
                        helper.showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier linked to drug successfully.");
                    }

                    @Override
                    protected void failed() {
                        // Hide loading dialog on task failure
                        Platform.runLater(loadingDialog::hideLoading);
                        helper.showAlert(Alert.AlertType.ERROR, "Error", "Could not link supplier to drug.");
                    }
                };

                // Start the background task
                new Thread(task).start();
            }
            return null;
        });

        // Show the dialog and wait for user action
        dialog.showAndWait();
    }
    @FXML
    private void viewPurchaseHistory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Purchase History");
        dialog.setHeaderText("Enter Drug Code:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Retrieving purchase history, please wait...");
            Task<List<Purchase>> task = new Task<>() {
                @Override
                protected List<Purchase> call() {
                    Platform.runLater(loadingDialog::showLoading);
                    return pharmacyManagement.viewPurchaseHistory(result.get());
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(loadingDialog::hideLoading);

                    List<Purchase> purchases = getValue();
                    String[] columnNames = {"Quantity", "Total Amount", "DateTime", "Buyer"};
                    TableView<Purchase> table = new TableView<>();
                    helper.createTable(table,columnNames, purchases);
                    helper.showAlert(Alert.AlertType.INFORMATION, "Purchase History", table);
                }
            };
            new Thread(task).start();
        }
    }
    @FXML
    private void addSales() {
        // Creates Text Fields for customer details
        TextField buyerField = new TextField();
        TextField contactField = new TextField();

        // Create Table for multiple sales input
        TableView<Purchase> tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<Purchase, String> drugCodeCol = new TableColumn<>("Drug Code");
        drugCodeCol.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        drugCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        drugCodeCol.setMinWidth(120);
        drugCodeCol.setStyle("-fx-alignment: CENTER;");
        drugCodeCol.setOnEditCommit(event -> event.getRowValue().setDrugCode(event.getNewValue()));

        TableColumn<Purchase, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setMinWidth(120);
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setOnEditCommit(event -> event.getRowValue().setQuantity(event.getNewValue()));

        tableView.getColumns().addAll(drugCodeCol, quantityCol);

        Button addRowButton = new Button("Add Row");
        addRowButton.setOnAction(event -> {
            tableView.getItems().add(new Purchase());
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Buyer:"), 0, 0);
        grid.add(buyerField, 1, 0);
        grid.add(new Label("Phone Number:"), 0, 1);
        grid.add(contactField, 1, 1);
        grid.add(new Label("Sales:"), 0, 2);
        grid.add(tableView, 0, 3, 2, 1);
        grid.add(addRowButton, 0, 4);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Sales");
        dialog.getDialogPane().setContent(grid);
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Adding sales, please wait...");
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        String buyer = buyerField.getText();
                        String contactInfo = contactField.getText();

                        List<Purchase> purchases = tableView.getItems();
                        for (Purchase purchase : purchases) {
                            purchase.setBuyerName(buyer);
                            purchase.setContactInfo(contactInfo);
                            purchase.setDateTime(LocalDateTime.now());
                        }

                        Platform.runLater(loadingDialog::showLoading);
                        pharmacyManagement.addSales(purchases);

                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            loadingDialog.hideLoading();
                            helper.showAlert(Alert.AlertType.INFORMATION, "Success", "Purchases added successfully");
                        });
                    }

                    @Override
                    protected void failed() {
                        Platform.runLater(() -> {
                            loadingDialog.hideLoading();
                            Throwable throwable = getException();
                            LOGGER.log(Level.SEVERE, "Error processing purchases", throwable);
                            helper.showAlert(Alert.AlertType.ERROR, "Error", "Could not process purchases: " + throwable.getMessage());
                        });
                    }
                };
                new Thread(task).start();
            }
            return null;
        });

        dialog.showAndWait();
    }
    @FXML
    private void viewSalesReport() {
        LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Retrieving sales report, please wait...");
        Task<List<Purchase>> task = new Task<>() {
            @Override
            protected List<Purchase> call() {
                Platform.runLater(loadingDialog::showLoading);
                return pharmacyManagement.getSalesReport();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(loadingDialog::hideLoading);

                List<Purchase> sales = getValue();
                String[] columnNames = {"Drug Code", "Quantity", "Total Amount", "DateTime", "Buyer"};

                TableView<Purchase> table = new TableView<>();
                helper.createTable(table, columnNames, sales);
                helper.showAlert(Alert.AlertType.INFORMATION, "Sales Report", table);
            }
        };
        new Thread(task).start();
    }
    @FXML
    private void deleteDrug() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Drug");
        dialog.setHeaderText("Enter drug code to delete:");
        Optional<String> drugCode = dialog.showAndWait();
        if (drugCode.isPresent()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Are you sure you want to delete the drug?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.get() == ButtonType.OK) {
                pharmacyManagement.removeDrug(drugCode.get());
            }
        }else{
            helper.showAlert(Alert.AlertType.ERROR, "Enter a drug code","Drug code can not be empty" );
        }
    }
    @FXML
    private void viewLinkedDrugAndSuppliers() {
        LoadingDialog loadingDialog = new LoadingDialog(primaryStage,
                "Retrieving linked drugs and suppliers, please wait...");
        Task<List<Drug>> task = new Task<>() {
            @Override
            protected List<Drug> call() {
                Platform.runLater(loadingDialog::showLoading);
                return pharmacyManagement.viewLinkedDrugAndSuppliers();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(loadingDialog::hideLoading);

                List<Drug> linkedDrugsAndSuppliers = getValue();
                String[] columnNames = { "Drug Code", "Drug Name", "Supplier Name", "Contact", "Location" };

                TableView<Object[]> table = new TableView<>();
                table.setPrefSize(600, 400);
                table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

                for (String columnName : columnNames) {
                    TableColumn<Object[], String> column = new TableColumn<>(columnName);
                    column.setCellValueFactory(cellData -> {
                        int index = helper.getIndex(columnName);
                        return new SimpleStringProperty(cellData.getValue()[index].toString());
                    });
                    column.setMinWidth(120);  // Set column width
                    column.setStyle("-fx-alignment: CENTER;"); // Center-align cell content
                    table.getColumns().add(column);
                }

                List<Object[]> dataList = new ArrayList<>();

                Iterator<Drug> drugIterator = linkedDrugsAndSuppliers.iterator();
                while (drugIterator.hasNext()) {
                    Drug drug = drugIterator.next();
                    List<Supplier> suppliers = drug.getSuppliers();

                    Iterator<Supplier> supplierIterator = suppliers.iterator();
                    while (supplierIterator.hasNext()) {
                        Supplier supplier = supplierIterator.next();
                        Object[] row = new Object[5];
                        row[0] = drug.getDrugCode();
                        row[1] = drug.getName();
                        row[2] = supplier.getName();
                        row[3] = supplier.getContactInfo();
                        row[4] = supplier.getLocation();
                        dataList.add(row);
                    }
                }

                table.getItems().addAll(dataList);
                table.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
                table.getStyleClass().add("table-view");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Linked Drugs and Suppliers");
                alert.getDialogPane().setContent(new ScrollPane(table));
                alert.showAndWait();
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void viewAllCustomers(){
        LoadingDialog loadingDialog = new LoadingDialog(primaryStage, "Retrieving customers, please wait...");
        Task<List<Customers>> task = new Task<>() {
            @Override
            protected List<Customers> call() {
                Platform.runLater(loadingDialog::showLoading);
                return pharmacyManagement.viewAllCustomers();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(loadingDialog::hideLoading);
                List<Customers> customers = getValue();
                String[] columnNames = {"Customer Name", "Phone Number", "Number Of Purchases"};

                TableView<Customers> table = new TableView<>();
                table.setPrefSize(600, 400);
                table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

                for (String columnName : columnNames) {
                    TableColumn<Customers, String> column = new TableColumn<>(columnName);
                    column.setCellValueFactory(new PropertyValueFactory<>(helper.convertToFieldName(columnName)));
                    column.setMinWidth(200);  // Set column width
                    column.setStyle("-fx-alignment: CENTER;"); // Center-align cell content
                    table.getColumns().add(column);
                }

                Iterator<Customers> iterator = customers.iterator();
                while (iterator.hasNext()) {
                    table.getItems().add(iterator.next());
                }
                table.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
                table.getStyleClass().add("table-view");

                helper.showAlert(Alert.AlertType.INFORMATION, "ALL Customers", table);
            }
        };
        new Thread(task).start();
    }
    @FXML
    private void exitApplication() {
        Platform.exit();
    }
}
