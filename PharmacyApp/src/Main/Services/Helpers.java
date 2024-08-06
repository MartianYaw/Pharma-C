package Main.Services;

import Main.Models.Customers;
import Main.Models.Pharmacy;
import Main.Models.Purchase;
import Main.Utils.DatabaseConnection;
import javafx.application.Platform;
import javafx.scene.control.*;

import java.sql.*;
import java.util.Objects;


public class Helpers {

    public Helpers() {
    }

    /**
     * Shows an alert dialog with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title     The title of the alert dialog.
     * @param content   The message to be displayed in the alert dialog.
     */
    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(()->{
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void showAlert(Alert.AlertType alertType, String Header, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(Header);
        alert.setContentText(content);
        passStyles( alert);
        alert.showAndWait();
    }
    public void showError(String title, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void passStyles(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
        dialogPane.getStyleClass().add("about-pane-smaller");

        if (dialogPane.getHeader() != null) {
            dialogPane.getHeader().getStyleClass().add("alert-header");
        }

        if (dialogPane.getContent() != null) {
            dialogPane.getContent().getStyleClass().add("alert-content");
        }

        alert.showAndWait();
    }


    public void addPurchaseToDatabase(Purchase purchase, double totalAmount, Connection conn, int customerId) throws SQLException {
        String sql = "INSERT INTO Purchases (drugCode, quantity, totalAmount, dateTime, customerId) VALUES ( ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, purchase.getDrugCode());
            pstmt.setInt(2, purchase.getQuantity());
            pstmt.setDouble(3, totalAmount);
            pstmt.setTimestamp(4, Timestamp.valueOf(purchase.getDateTime()));
            pstmt.setInt(5, customerId);
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

    boolean validDrug(String drugCode){
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

    public int getOrAddCustomer(String name, String contactInfo, Connection conn, Pharmacy pharmacy) throws SQLException {
        String checkCustomerSql = "SELECT customerId FROM Customers WHERE contactInfo = ?";
        try (PreparedStatement checkCustomerStmt = conn.prepareStatement(checkCustomerSql)) {
            checkCustomerStmt.setString(1, contactInfo);
            try (ResultSet rs = checkCustomerStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customerId");
                }
            }
        }
        String addCustomerSql = "INSERT INTO Customers (name, contactInfo) VALUES (?,?)";
        try (PreparedStatement addCustomerStmt = conn.prepareStatement(addCustomerSql, Statement.RETURN_GENERATED_KEYS)) {
            addCustomerStmt.setString(1, name);
            addCustomerStmt.setString(2, contactInfo);
            int affectedRows = addCustomerStmt.executeUpdate();
            if (affectedRows > 0) {
                pharmacy.addCustomer(new Customers(name, contactInfo, 0));
                try (ResultSet generatedKeys = addCustomerStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
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
            case "Buyer", "Customer Name" -> "buyerName";
            case "Phone Number" -> "contactInfo";
            case "Number Of Purchases" -> "purchases";
            case "Location" -> "location";
            default -> throw new IllegalArgumentException("Invalid column name: " + columnName);
        };
    }
}
