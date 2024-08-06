//Controller
package Main.Controllers;

import Main.Models.Pharmacy;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import Main.Models.Drug;
import Main.Services.Helpers;
import Main.Services.PharmacyManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ViewDrugsController {
    @FXML
    private TableView<Drug> drugsTable;
    @FXML
    private TableColumn<Drug, String> drugCodeColumn;
    @FXML
    private TableColumn<Drug, String> nameColumn;
    @FXML
    private TableColumn<Drug, String> categoryColumn;
    @FXML
    private TableColumn<Drug, Double> priceColumn;
    @FXML
    private TableColumn<Drug, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Drug, Void> deleteColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;

    private final Helpers helper;
    private final PharmacyManagement pharmacyManagement;
    private final Pharmacy pharmacy;

    public ViewDrugsController() {
        helper = new Helpers();
        pharmacyManagement = new PharmacyManagement();
        pharmacy = new Pharmacy();
    }

    @FXML
    public void initialize() {
        drugCodeColumn.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        deleteColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Drug, Void> call(TableColumn<Drug, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Drug drug = getTableView().getItems().get(getIndex());
                            deleteDrug(drug);
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
                };
            }
        });
        viewAllDrugs();
        searchButton.setOnAction(event -> searchDrugs());
    }

    @FXML
    private void viewAllDrugs() {
        Task<Map<String, Drug>> task = new Task<>() {
            @Override
            protected Map<String, Drug> call() {
                return pharmacy.getDrugs();
            }
            @Override
            protected void succeeded() {
                List<Drug> drugList = new ArrayList<>(getValue().values());
                Platform.runLater(() -> drugsTable.getItems().setAll(drugList));
            }
            @Override
            protected void failed() {
                Platform.runLater(() -> helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve drugs from db."));
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void searchDrugs() {
        String searchInput = searchField.getText().trim();

        if (!searchInput.isEmpty()) {
            Task<List<Drug>> task = new Task<>() {
                @Override
                protected List<Drug> call() {
                    return pharmacyManagement.searchDrug(searchInput);
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        List<Drug> drugs = getValue();
                        if (drugs == null || drugs.isEmpty()) {
                            helper.showAlert(Alert.AlertType.INFORMATION, "No Results", "No drugs found");
                            viewAllDrugs();
                        } else {
                            drugsTable.getItems().setAll(drugs);
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to search drugs.");
                        viewAllDrugs();
                    });
                }
            };
            new Thread(task).start();
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Search field cannot be empty.");
            viewAllDrugs();
        }
    }

    private void deleteDrug(Drug drug) {
        String drugCode = drug.getDrugCode();

        if (drugCode != null && !drugCode.isEmpty()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Are you sure you want to delete the drug with code: " + drugCode + "?");
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception{
                        pharmacyManagement.removeDrug(drugCode);
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            viewAllDrugs();
                            helper.showAlert(Alert.AlertType.INFORMATION, "Success", "Drug deleted successfully.");
                        });
                    }

                    @Override
                    protected void failed() {
                        Throwable exception = getException();
                        exception.printStackTrace();
                        Platform.runLater(() -> helper.showAlert(Alert.AlertType.ERROR, "Error", exception.getMessage()));
                    }
                };
                new Thread(task).start();
            }
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Drug code cannot be empty.");
        }
    }
}
