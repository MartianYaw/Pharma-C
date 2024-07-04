package Main;

import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PharmacyManagement {
    private static final Logger LOGGER = Logger.getLogger(PharmacyManagement.class.getName());
    private final Map<String, Drug> drugMap;

    private final Helpers helper = new Helpers();

    public PharmacyManagement() {
        drugMap = new HashMap<>();
    }

    /**
     * Adds a new drug to the pharmacy system and database.
     *
     * @param drug The drug object containing details of the drug to be added.
     */
    public void addDrug(Drug drug) {
        drugMap.put(drug.getDrugCode(), drug);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Drugs (drugCode, name, category, price, stockQuantity) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, drug.getDrugCode());
                pstmt.setString(2, drug.getName());
                pstmt.setString(3, drug.getCategory());
                pstmt.setDouble(4, drug.getPrice());
                pstmt.setInt(5, drug.getStockQuantity());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding drug to database", e);
            helper.showAlert(AlertType.ERROR, "Error", "Could not add drug to database.");
        }
    }

    /**
     * Adds a new sale (purchase) to the pharmacy system and database.
     *
     * @param purchase The purchase object containing details of the sale.
     */
    public void addSale(Purchase purchase) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT price, stockQuantity FROM Drugs WHERE drugCode = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, purchase.getDrugCode());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        double price = rs.getDouble("price");
                        int stockQuantity = rs.getInt("stockQuantity");

                        double totalAmount = price * purchase.getQuantity();
                        purchase.setTotalAmount(totalAmount);

                        if (stockQuantity >= purchase.getQuantity()) {
                            helper.addPurchaseToDatabase(purchase, totalAmount, conn);
                            helper.updateDrugStock(purchase, conn);
                        } else {
                            helper.showAlert(AlertType.WARNING, "Warning",
                                    "Not enough stock for drug with code " + purchase.getDrugCode());
                        }
                    } else {
                        helper.showAlert(AlertType.WARNING, "Warning",
                                "Drug with code " + purchase.getDrugCode() + " does not exist.");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding sale to database", e);
            helper.showAlert(AlertType.ERROR, "Error", "Could not add sale to database.");
        }
    }



    /**
     * Searches for a drug by its name in the database and returns the drug details.
     *
     * @param drugName The name of the drug to be searched.
     * @return The Drug object if found, otherwise null.
     */
    public Drug searchDrugByName(String drugName) {
        helper.loadDrugsFromDatabase(drugMap, LOGGER);

        // Convert drugMap values to a list and sort it by name (case-insensitive)
        List<Drug> drugList = new ArrayList<>(drugMap.values());
        drugList.sort(Comparator.comparing(Drug::getName, String.CASE_INSENSITIVE_ORDER));

        // Use binary search to find the drug by name (case-insensitive)
        return BinarySearch.binarySearchByName(drugList, drugName);
    }

    /**
     * Searches for a drug by its code in the database and returns the drug details.
     *
     * @param drugCode The code of the drug to be searched.
     * @return The Drug object if found, otherwise null.
     */
    public Drug searchDrugByCode(String drugCode) {
        helper.loadDrugsFromDatabase(drugMap, LOGGER);

        // Convert drugMap values to a list and sort it by drug code
        List<Drug> drugList = new ArrayList<>(drugMap.values());
        drugList.sort(Comparator.comparing(Drug::getDrugCode));

        // Use binary search to find the drug by code
        return BinarySearch.binarySearchByCode(drugList, drugCode);
    }




    /**
     * Retrieves a list of all drugs in the pharmacy system and sorts them.
     *
     * @return A sorted list of Drug objects.
     */
    public List<Drug> viewAllDrugs() {
        List<Drug> drugs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Drugs";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String drugCode = rs.getString("drugCode");
                    String name = rs.getString("name");
                    String category = rs.getString("category");
                    double price = rs.getDouble("price");
                    int stockQuantity = rs.getInt("stockQuantity");
                    Drug drug = new Drug(drugCode, name, category, price, stockQuantity);
                    drugs.add(drug);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing all drugs", e);
        }

        MergeSort.mergeSort(drugs, 0, drugs.size() - 1);
        return drugs;
    }

    /**
     * Adds a new supplier to the pharmacy system and database.
     *
     * @param supplier The supplier object containing details of the supplier to be
     *                 added.
     */
    public void addSupplier(Supplier supplier) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Suppliers (name, location, contactInfo) VALUES ( ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, supplier.name());
                pstmt.setString(2, supplier.location());
                pstmt.setString(3, supplier.contactInfo());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding supplier to database", e);
            helper.showAlert(AlertType.ERROR, "Error", "Could not add supplier to database.");
        }
    }

    public void removeDrug(String drugCode) {
        if (helper.validDrug(drugCode)) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                helper.deleteDrugAssociations(drugCode, conn);
                String sql2 = "DELETE FROM Drugs WHERE drugCode = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                    pstmt2.setString(1, drugCode);
                    pstmt2.executeUpdate();
                    helper.showAlert(AlertType.INFORMATION, "Success", "Drug deleted successfully.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error removing drug from database", e);
                helper.showAlert(AlertType.ERROR, "Error", "Could not remove drug from database.");
            }
        } else {
            helper.showAlert(AlertType.WARNING, "Warning", "Drug with code " + drugCode + " does not exist.");
        }
    }



    /**
     * Links a supplier to a specific drug in the pharmacy system and database.
     *
     * @param drugCode The code of the drug to be linked.
     * @param supplier The supplier object containing details of the supplier to be
     *                 linked.
     */
    public void linkSupplierToDrug(String drugCode, Supplier supplier) {
        if (helper.validDrug(drugCode)) {
            addSupplier(supplier);
            try (Connection conn = DatabaseConnection.getConnection()) {
                int supplierId = getSupplierId(supplier, conn);
                if (supplierId != -1) {
                    String sql = "INSERT INTO DrugSuppliers (drugCode, supplierId) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, drugCode);
                        pstmt.setInt(2, supplierId);
                        pstmt.executeUpdate();
                    }
                } else {
                    helper.showAlert(AlertType.WARNING, "Warning", "Could not find supplier ID.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error linking supplier to drug in database", e);
                helper.showAlert(AlertType.ERROR, "Error", "Could not link supplier to drug.");
            }
        } else {
            helper.showAlert(AlertType.WARNING, "Warning", "Drug with code " + drugCode + " does not exist.");
        }
    }


    private int getSupplierId(Supplier supplier, Connection conn) throws SQLException {
        String sql = "SELECT supplierId FROM Suppliers WHERE name = ? AND location = ? AND contactInfo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.name());
            pstmt.setString(2, supplier.location());
            pstmt.setString(3, supplier.contactInfo());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("supplierId");
                }
            }
        }
        return -1;
    }



    /**
     * Retrieves the purchase history for a specific drug.
     *
     * @param drugCode The code of the drug whose purchase history is to be
     *                 retrieved.
     * @return A list of Purchase objects representing the purchase history.
     */
    public List<Purchase> viewPurchaseHistory(String drugCode) {
        List<Purchase> purchaseHistory = new ArrayList<>();
        if (helper.validDrug(drugCode)) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM Purchases WHERE drugCode = ? ORDER BY dateTime DESC";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, drugCode);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            getFromDb(purchaseHistory, rs, drugCode);
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error viewing purchase history", e);
            }
        } else {
            helper.showAlert(AlertType.WARNING, "Warning", "Drug with code " + drugCode + " does not exist.");
        }
        return purchaseHistory;
    }

    /**
     * Retrieves the complete sales report for all drugs.
     *
     * @return A list of Purchase objects representing the sales report.
     */
    public List<Purchase> getSalesReport() {
        List<Purchase> salesReports = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Purchases ORDER BY dateTime DESC ";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String drugCode = rs.getString("drugCode");
                    getFromDb(salesReports, rs, drugCode);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating sales report", e);
        }
        return salesReports;
    }

    /**
     * Helper method to extract purchase details from the database and add to a
     * list.
     *
     * @param purchaseList The list to add the purchase details to.
     * @param rs           The ResultSet containing the purchase details.
     * @param drugCode     The code of the drug.
     * @throws SQLException If there is an error accessing the database.
     */
    private void getFromDb(List<Purchase> purchaseList, ResultSet rs, String drugCode)
            throws SQLException {
        int quantity = rs.getInt("quantity");
        double totalAmount = rs.getDouble("totalAmount");
        LocalDateTime dateTime = rs.getTimestamp("dateTime").toLocalDateTime();
        String buyer = rs.getString("buyer");
        Purchase purchase = new Purchase(drugCode, quantity, totalAmount, dateTime, buyer);
        purchaseList.add(purchase);
    }

    /**
     * Returns a list of drugs along with their suppliers.
     *
     * @return A list of Drug objects, each containing drug details and associated
     *         suppliers.
     */
    public List<Drug> viewLinkedDrugAndSuppliers() {
        List<Drug> drugSuppliersList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT d.drugCode, d.name AS drugName, d.category, d.price, d.stockQuantity, " +
                    "s.supplierId, s.name AS supplierName, s.location, s.contactInfo " +
                    "FROM Drugs d " +
                    "JOIN DrugSuppliers ds ON d.drugCode = ds.drugCode " +
                    "JOIN Suppliers s ON ds.supplierId = s.supplierId " +
                    "ORDER BY d.name, d.drugCode, s.supplierId";

            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = pstmt.executeQuery();

            String currentDrugCode = "";
            Drug drug = null;

            while (rs.next()) {
                String drugCode = rs.getString("drugCode");

                if (!drugCode.equals(currentDrugCode)) {
                    if (drug != null) {
                        drugSuppliersList.add(drug);
                    }

                    String drugName = rs.getString("drugName");
                    String category = rs.getString("category");
                    double price = rs.getDouble("price");
                    int stockQuantity = rs.getInt("stockQuantity");

                    drug = new Drug(drugCode, drugName, category, price, stockQuantity);
                    currentDrugCode = drugCode;
                }
                String supplierName = rs.getString("supplierName");
                String location = rs.getString("location");
                String contactInfo = rs.getString("contactInfo");

                Supplier supplier = new Supplier(supplierName, location, contactInfo);
                assert drug != null;
                drug.addSupplier(supplier);
            }

            if (drug != null) {
                drugSuppliersList.add(drug);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing linked drugs and suppliers", e);
        }

        return drugSuppliersList;
    }
}
