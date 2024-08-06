//Controller
package Main.Controllers;

import Main.Models.Pharmacy;
import Main.Utils.BubbleSort;
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

/**
 * This JavaFX controller manages the user interface for viewing, searching,
 * and deleting drugs in the pharmacy database.
 */
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
    @FXML
    private ProgressIndicator loadingIndicator;

    private final Helpers helper;
    private final PharmacyManagement pharmacyManagement;
    private final Pharmacy pharmacy;

    /**
     * Constructor initializes helper, pharmacy management, and pharmacy objects.
     */
    public ViewDrugsController() {
        helper = new Helpers();
        pharmacyManagement = new PharmacyManagement();
        pharmacy = new Pharmacy();
    }

    /**
     * Initializes the controller, sets up the table columns, and loads all drugs.
     */
    @FXML
    public void initialize() {
        //setCellValueFactory sets cell value for each column to bind them to the corresponding properties in the Drug model.

        drugCodeColumn.setCellValueFactory(new PropertyValueFactory<>("drugCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));


        //Sets a custom cell in the delete column.
        //Adds a delete button to each row. Clicking the button triggers the deleteDrug method for the corresponding drug.
        deleteColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Drug, Void> call(TableColumn<Drug, Void> param) {
                return new TableCell<>() {
                    //creates the button and add an action
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Drug drug = getTableView().getItems().get(getIndex());
                            deleteDrug(drug);
                        });
                    }

                    //Shows the button
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

        //Calls viewAllDrugs to load all drugs into the table.
        viewAllDrugs();

        //Sets the searchButton to call the searchDrugs method when clicked.
        searchButton.setOnAction(event -> searchDrugs());
    }

    /**
     * Loads all drugs from the database and displays them in the table.
     * Method Annotation: Indicates this method is called from FXML.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Background Task:
         * Creates a Task to retrieve all drugs from the database in a background thread.
         * call Method: Calls pharmacy.getDrugs() to retrieve the drugs.
         * succeeded Method: On success, converts the result to a list, sorts it by name, and updates the table on the JavaFX Application Thread.
         * failed Method: On failure, hides the loading indicator and shows an error alert.
         * Start Task: Starts the task in a new thread.
     */
    @FXML
    private void viewAllDrugs() {
        loadingIndicator.setVisible(true);
        Task<Map<String, Drug>> task = new Task<>() {
            @Override
            protected Map<String, Drug> call() {
                return pharmacy.getDrugs();
            }
            @Override
            protected void succeeded() {
                List<Drug> drugList = new ArrayList<>(getValue().values());
                BubbleSort.bubbleSortName(drugList);
                Platform.runLater(() -> {
                    drugsTable.getItems().setAll(drugList);
                    loadingIndicator.setVisible(false);
                });
            }
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve drugs from db.");
                });
            }
        };
        new Thread(task).start();
    }

    /**
     * Searches for drugs based on the input in the search field and displays the results.
     * Method Annotation: Indicates this method is called from FXML.
     * Loading Indicator: Shows the loading indicator while the task runs.
     * Input Validation: Checks if the search field is not empty.
     * Background Task:
         *  Creates a Task to search for drugs based on the input in a background thread.
         *  call Method: Calls pharmacyManagement.searchDrug(searchInput) to search for drugs.
         *  succeeded Method: On success, updates the table with the search results or shows a "No Results" alert if none are found.
         *  failed Method: On failure, hides the loading indicator, shows an error alert, and reloads all drugs.
         *  Start Task: Starts the task in a new thread.
         *  Empty Search Field: If the search field is empty, shows an error alert, reloads all drugs, and hides the loading indicator.
     */
    @FXML
    private void searchDrugs() {
        loadingIndicator.setVisible(true);
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
                            loadingIndicator.setVisible(false);
                        } else {
                            drugsTable.getItems().setAll(drugs);
                            loadingIndicator.setVisible(false);
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        helper.showAlert(Alert.AlertType.ERROR, "Error", "Failed to search drugs.");
                        viewAllDrugs();
                        loadingIndicator.setVisible(false);
                    });
                }
            };
            new Thread(task).start();
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Search field cannot be empty.");
            viewAllDrugs();
            loadingIndicator.setVisible(false);
        }
    }

    /**
     * Deletes the specified drug from the database after confirming with the user.
     *Method Annotation: Indicates this is a private method.
     * Drug Code Validation: Checks if the drug code is not null or empty.
     * Confirmation Dialog: Shows a confirmation dialog to confirm the deletion.
     * User Confirmation: If the user confirms, starts the deletion process.
     * Background Task:
         * Creates a Task to delete the drug from the database in a background thread.
         * call Method: Calls pharmacyManagement.removeDrug(drugCode) to delete the drug.
         * succeeded Method: On success, reloads all drugs, hides the loading indicator, and shows a success alert.
         * failed Method: On failure, prints the stack trace, hides the loading indicator, and shows an error alert.
         * Start Task: Starts the task in a new thread.
         * Invalid Drug Code: If the drug code is invalid, shows an error alert and hides the loading indicator.
     *
     * @param drug the drug to be deleted
     *
     */
    private void deleteDrug(Drug drug) {
        String drugCode = drug.getDrugCode();
        if (drugCode != null && !drugCode.isEmpty()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Are you sure you want to delete the drug with code: " + drugCode + "?");
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                loadingIndicator.setVisible(true);
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
                            loadingIndicator.setVisible(false);
                            helper.showAlert(Alert.AlertType.INFORMATION, "Success", "Drug deleted successfully.");
                        });
                    }

                    @Override
                    protected void failed() {
                        Throwable exception = getException();
                        exception.printStackTrace();
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            helper.showAlert(Alert.AlertType.ERROR, "Error", exception.getMessage());
                        });
                    }
                };
                new Thread(task).start();
            }
        } else {
            helper.showAlert(Alert.AlertType.ERROR, "Error", "Drug code cannot be empty.");
            loadingIndicator.setVisible(false);
        }
    }
}
