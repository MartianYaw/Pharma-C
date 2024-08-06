package Main.Services;

import Main.Models.*;
import Main.Utils.BubbleSort;
import Main.Utils.DatabaseConnection;
import Main.Utils.LinearSearch;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PharmacyManagement {
    private final Helpers helper = new Helpers();
    private final Pharmacy pharmacy = new Pharmacy();

    public PharmacyManagement() {
    }

    /**
     * Adds a new drug to the pharmacy system and database.
     *
     * @param drug The drug object containing details of the drug to be added.
     */
    public void addDrug(Drug drug) throws Exception{
        pharmacy.addDrug(drug);
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
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Adds a new sale (purchase) to the pharmacy system and database.
     *
     * @param purchases The purchase list containing details of the sale.
     */
    public void addSales(List<Purchase> purchases) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (purchases.isEmpty()) {
                throw new Exception("No purchases provided.");
            }

            for (Purchase purchase : purchases) {
                int customer = helper.getOrAddCustomer(purchase.getBuyerName(), purchase.getContactInfo(), conn, pharmacy);
                System.out.println(customer);
                if (customer == -1) {
                    throw new Exception("No customer provided.");
                }else{
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
                                    pharmacy.addPurchase(purchase);
                                    helper.addPurchaseToDatabase(purchase, totalAmount, conn, customer);
                                    helper.updateDrugStock(purchase, conn);
                                } else {
                                    throw new Exception("Not enough stock for drug with code " + purchase.getDrugCode());
                                }
                            } else {
                                throw new Exception("Drug with code " + purchase.getDrugCode() + " does not exist.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Could not add sales to database.", e);
        }
    }

    /**
     * Searches for a drug by its name in the database and returns the drug details.
     *
     * @param drugName The name of the drug to be searched.
     * @return The Drug object if found, otherwise null.
     */
    public List<Drug> searchDrugByName(String drugName) {
        Map<String, Drug> drugs = pharmacy.getDrugs();
        // Convert drugMap values to a list and sort it by name (case-insensitive)
        List<Drug> drugList = new ArrayList<>(drugs.values());
        BubbleSort.bubbleSortName(drugList);
        // Use binary search to find the drug by name (case-insensitive)
        return LinearSearch.linearSearchByName(drugList, drugName);
    }

    /**
     * Searches for a drug by its code in the database and returns the drug details.
     *
     * @param drugCode The code of the drug to be searched.
     * @return The Drug object if found, otherwise null.
     */
    public List<Drug> searchDrugByCode(String drugCode) {
        Map<String, Drug> drugs = pharmacy.getDrugs();
        List<Drug> drugList = new ArrayList<>(drugs.values());
        BubbleSort.bubbleSortCode(drugList);
        return LinearSearch.linearSearchByCode(drugList, drugCode);
    }

    public List<Drug> searchDrug(String searchTerm) {
        List<Drug> drugs = new ArrayList<>();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return drugs;
        }
        List<Drug> drugByName = searchDrugByName(searchTerm);
        if (!drugByName.isEmpty()) {
            drugs.addAll(drugByName);
        }
        if (drugByName.isEmpty()) {
            List<Drug> drugByCode = searchDrugByCode(searchTerm);
            if (!drugByCode.isEmpty()) {
                drugs.addAll(drugByCode);
            }
        }
        return drugs;
    }

    /**
     * Searches for a drug by its code in the database and returns the drug details.
     *
     * @param Location The location of the supplier to be searched.
     * @return A list of suppliers if found.
     */
    public List<LinkedData> searchDrugSupplier(String Location) {
        List<LinkedData> suppliers = pharmacy.getSuppliers();
        BubbleSort.sortSuppliers(suppliers);
        return LinearSearch.searchSuppliersByLocation(suppliers, Location);
    }


    /**
     * Adds a new supplier to the pharmacy system and database.
     *
     * @param supplier The supplier object containing details of the supplier to be
     *                 added.
     */
    public void addSupplier(Supplier supplier, String drugCode) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Suppliers (name, location, contactInfo) VALUES ( ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, supplier.getName());
                pstmt.setString(2, supplier.getLocation());
                pstmt.setString(3, supplier.getContactInfo());
                pstmt.executeUpdate();
            }
            String drugName = pharmacy.getDrugs().get(drugCode).getName();
            pharmacy.addSupplier(new LinkedData(drugCode, drugName, supplier.getName(), supplier.getContactInfo(), supplier.getLocation()));
        } catch (SQLException e) {
            helper.showError("Error", "Could not add supplier to database.");
            e.printStackTrace();
        }
    }

    public void removeDrug(String drugCode) throws Exception{
        if (helper.validDrug(drugCode)) {
            pharmacy.removeDrug(drugCode);
            try (Connection conn = DatabaseConnection.getConnection()) {
                helper.deleteDrugAssociations(drugCode, conn);
                String sql2 = "DELETE FROM Drugs WHERE drugCode = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                    pstmt2.setString(1, drugCode);
                    pstmt2.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new Exception("Could not remove drug from database.");
            }
        } else {
            throw new Exception("Drug with code " + drugCode + " does not exist.");
        }
    }

    /**
     * Links a supplier to a specific drug in the pharmacy system and database.
     *
     * @param drugCode The code of the drug to be linked.
     * @param supplier The supplier object containing details of the supplier to be
     *                 linked.
     */
    public void linkSupplierToDrug(String drugCode, Supplier supplier) throws Exception{
        if (helper.validDrug(drugCode)) {
            addSupplier(supplier, drugCode);
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
                    throw new Exception("Could not find supplier ID.");
                }
            } catch (SQLException e) {
                throw new Exception("Could not link supplier to drug.");
            }
        } else {
            throw new Exception("Drug with code " + drugCode + " does not exist.");
        }
    }

    private int getSupplierId(Supplier supplier, Connection conn) throws SQLException {
        String sql = "SELECT supplierId FROM Suppliers WHERE name = ? AND location = ? AND contactInfo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getLocation());
            pstmt.setString(3, supplier.getContactInfo());
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
    public Map<LocalDateTime, Purchase> viewPurchaseHistory(String drugCode) throws Exception{
        Map<LocalDateTime, Purchase> allPurchases = pharmacy.getPurchases();
        Map<LocalDateTime, Purchase> purchaseHistory = new HashMap<>();
        if (helper.validDrug(drugCode)) {
            Iterator<Map.Entry<LocalDateTime, Purchase>> iterator = allPurchases.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDateTime, Purchase> entry = iterator.next();
                if (entry.getValue().getDrugCode().equals(drugCode)) {
                    purchaseHistory.put(entry.getKey(), entry.getValue());
                }
            }
            BubbleSort.sortPurchasesByDateTime(purchaseHistory);
        }else{
            throw new Exception("Drug with code " + drugCode + " does not exist.");
        }
        return purchaseHistory;
    }



}
