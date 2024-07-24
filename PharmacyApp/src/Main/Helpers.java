package Main;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Helpers {

    public Helpers() {

    }

    public void ifPresent(Drug drug) {
        if (drug != null) {
            String sb = String.format("Name: %s\n", drug.getName()) +
                    String.format("Category: %s\n", drug.getCategory()) +
                    String.format("Price: %.2f\n", drug.getPrice()) +
                    String.format("Stock Quantity: %d\n", drug.getStockQuantity());
            showAlert(Alert.AlertType.INFORMATION,  "Drug found:", "Drug Information", sb );
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            showAlert(alert.getAlertType(), "Error", "Drug not Found");
        }
    }

    /**
     * Shows an alert dialog with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title     The title of the alert dialog.
     * @param content   The message to be displayed in the alert dialog.
     */
    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        passStyles( alert, "about-pane-smaller");
    }

    public void showAlert(Alert.AlertType alertType, String Header, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(Header);
        alert.setContentText(content);
        passStyles( alert, "about-pane-smaller");
    }
    public void showAlert(Alert.AlertType alertType, String title, Node table) {
        Alert alert = new Alert(alertType);
        alert.getDialogPane().setContent(new ScrollPane(table));
        alert.setTitle(title);
        passStyles(alert, "about-pane");
    }


    private void passStyles(Alert alert, String style) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
        dialogPane.getStyleClass().add(style);

        if (dialogPane.getHeader() != null) {
            dialogPane.getHeader().getStyleClass().add("alert-header");
        }

        if (dialogPane.getContent() != null) {
            dialogPane.getContent().getStyleClass().add("alert-content");
        }

        alert.showAndWait();
    }

    /**
     * Loads all drugs from the database into the drugMap.
     */
    public void loadDrugsFromDatabase(Map<String, Drug> drugMap,Logger logger) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Drugs";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                drugMap.clear();
                while (rs.next()) {
                    String drugCode = rs.getString("drugCode");
                    String name = rs.getString("name");
                    String category = rs.getString("category");
                    double price = rs.getDouble("price");
                    int stockQuantity = rs.getInt("stockQuantity");
                    Drug drug = new Drug(drugCode, name, category, price, stockQuantity);
                    drugMap.put(drugCode, drug);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading drugs from database", e);
        }
    }

    public void addPurchaseToDatabase(Purchase purchase, double totalAmount, Connection conn) throws SQLException {
        String sql = "INSERT INTO Purchases (drugCode, quantity, totalAmount, dateTime, buyer) VALUES ( ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, purchase.getDrugCode());
            pstmt.setInt(2, purchase.getQuantity());
            pstmt.setDouble(3, totalAmount);
            pstmt.setTimestamp(4, Timestamp.valueOf(purchase.getDateTime()));
            pstmt.setString(5, purchase.getBuyer());
            pstmt.executeUpdate();
        }
    }

    public void updateDrugStock(Purchase purchase, Connection conn) throws SQLException {
        String updateStockSql = "UPDATE Drugs SET stockQuantity = stockQuantity - ? WHERE drugCode = ?";
        try (PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql)) {
            updateStockStmt.setInt(1, purchase.getQuantity());
            updateStockStmt.setString(2, purchase.getDrugCode());
            updateStockStmt.executeUpdate();
        }
    }

    public void deleteDrugAssociations(String drugCode, Connection conn) throws SQLException {
        String sql = "DELETE FROM DrugSuppliers WHERE drugCode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, drugCode);
            pstmt.executeUpdate();
        }

        String sql1 = "DELETE FROM Purchases WHERE drugCode = ?";
        try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
            pstmt1.setString(1, drugCode);
            pstmt1.executeUpdate();
        }
    }

    boolean validDrug(String drugCode) {
        boolean drugExists = false;
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if the drug exists in the database
            String checkDrugSql = "SELECT COUNT(*) FROM Drugs WHERE drugCode = ?";
            PreparedStatement checkDrugStmt = conn.prepareStatement(checkDrugSql);
            checkDrugStmt.setString(1, drugCode);
            ResultSet rs = checkDrugStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                drugExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drugExists;
    }

    public void createTable(TableView<Purchase> table, String[] columnNames, List<Purchase> purchase){
        table.setPrefSize(600, 400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for (String columnName : columnNames) {
            TableColumn<Purchase, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<>(convertToFieldName(columnName)));
            column.setMinWidth(120);  // Set column width
            column.setStyle("-fx-alignment: CENTER;"); // Center-align cell content
            table.getColumns().add(column);
        }

        Iterator<Purchase> iterator = purchase.iterator();
        while (iterator.hasNext()) {
            table.getItems().add(iterator.next());
        }

        table.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
        table.getStyleClass().add("table-view");
    }

    public String convertToFieldName(String columnName) {
        return switch (columnName) {
            case "Drug Code" -> "drugCode";
            case "Name" -> "name";
            case "Category" -> "category";
            case "Price" -> "price";
            case "Stock Quantity" -> "stockQuantity";
            case "Quantity" -> "quantity";
            case "Total Amount" -> "totalAmount";
            case "DateTime" -> "dateTime";
            case "Buyer" -> "buyer";
            default -> throw new IllegalArgumentException("Invalid column name: " + columnName);
        };
    }

    public int getIndex(String ColumnName){
        return switch(ColumnName){
            case "Drug Code" -> 0;
            case "Drug Name" -> 1;
            case "Supplier Name" -> 2;
            case "Contact" -> 3;
            case "Location" -> 4;
            default -> throw new IllegalArgumentException("Invalid column name: " + ColumnName);

        };
    }

}
