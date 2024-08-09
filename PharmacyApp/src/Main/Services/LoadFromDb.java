package Main.Services;

import Main.Models.Customers;
import Main.Models.Drug;
import Main.Models.LinkedData;
import Main.Models.Purchase;
import Main.Utils.BubbleSort;
import Main.Utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for loading data from the database.
 */
public class LoadFromDb {

    private final Helpers helper;

    public LoadFromDb() {
        helper = new Helpers();
    }

    /**
     * Retrieves a list of all drugs in the pharmacy system and sorts them.
     *
     * @return A hashmap of Drug objects.
     */
    public Map<String, Drug> viewAllDrugs() {
        Map<String, Drug> drugs = new HashMap<>();
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
                    drugs.put(drugCode, drug);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            helper.showError("Error", "Error viewing all drugs");
        }
        return drugs;
    }

    /**
     * Retrieves a list of all customers in the pharmacy system.
     *
     * @return A list of Customers objects.
     */
    public List<Customers> viewAllCustomers() {
        List<Customers> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Customers";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("customerId");
                    int purchases = 0;
                    try (Connection con = DatabaseConnection.getConnection()) {
                        String sequel = "SELECT COUNT(*) as 'totalPurchases' FROM Purchases WHERE customerId = ?";
                        try (PreparedStatement preparedStatement = con.prepareStatement(sequel)) {
                            preparedStatement.setInt(1, id);
                            ResultSet rss = preparedStatement.executeQuery();
                            while (rss.next()) {
                                purchases = rss.getInt("totalPurchases");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        helper.showError("Error", "Error viewing all customers");
                        e.printStackTrace();
                    }
                    String name = rs.getString("name");
                    String contactInfo = rs.getString("contactInfo");
                    Customers customer = new Customers(name, contactInfo, purchases);
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Retrieves the complete sales report for all drugs.
     *
     * @return A hashmap of Purchase objects representing the sales report.
     */
    public Map<LocalDateTime, Purchase> getSalesReport() {
        Map<LocalDateTime, Purchase> salesReports = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, c.name AS customerName, c.contactInfo AS customerContactInfo " +
                    "FROM Purchases p " +
                    "JOIN Customers c ON p.customerId = c.customerId " +
                    "ORDER BY p.dateTime";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                getFromDb(salesReports, rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            helper.showError("Error", "Something happened");
        }
        BubbleSort.sortPurchasesByDateTime(salesReports);
        return salesReports;
    }

    /**
     * Helper method to extract purchase details from the database and add to a
     * map.
     *
     * @param salesReports The map to add the purchase details to.
     * @param rs           The ResultSet containing the purchase details.
     * @throws SQLException If there is an error accessing the database.
     */
    private void getFromDb(Map<LocalDateTime, Purchase> salesReports, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Purchase purchase = new Purchase();
            purchase.setDrugCode(rs.getString("drugCode"));
            purchase.setQuantity(rs.getInt("quantity"));
            purchase.setTotalAmount(rs.getDouble("totalAmount"));
            purchase.setDateTime(rs.getTimestamp("dateTime").toLocalDateTime());
            purchase.setBuyerName(rs.getString("customerName"));
            purchase.setContactInfo(rs.getString("customerContactInfo"));
            salesReports.put(purchase.getDateTime(), purchase);
        }
    }

    /**
     * Returns a list of drugs along with their suppliers.
     *
     * @return A list of LinkedData objects, each containing drug details and associated
     *         suppliers.
     */
    public List<LinkedData> viewLinkedDrugAndSuppliers() {
        return getDrugsAndSuppliers();
    }

    /**
     * Retrieves a list of drugs along with their suppliers from the database.
     *
     * @return A list of LinkedData objects, each containing drug details and associated suppliers.
     */
    private List<LinkedData> getDrugsAndSuppliers() {
        List<LinkedData> drugSuppliersList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT d.drugCode, d.name AS drugName, d.category, d.price, d.stockQuantity, " +
                    "s.supplierId, s.name AS supplierName, s.location, s.contactInfo " +
                    "FROM Drugs d " +
                    "JOIN DrugSuppliers ds ON d.drugCode = ds.drugCode " +
                    "JOIN Suppliers s ON ds.supplierId = s.supplierId " +
                    "ORDER BY d.name, d.drugCode, s.supplierId";

            try (PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = pstmt.executeQuery()) {
                LinkedData linkedSupplier = null;

                while (rs.next()) {
                    String drugCode = rs.getString("drugCode");
                    String drugName = rs.getString("drugName");
                    String supplierName = rs.getString("supplierName");
                    String location = rs.getString("location");
                    String contactInfo = rs.getString("contactInfo");

                    if (linkedSupplier != null) {
                        drugSuppliersList.add(linkedSupplier);
                    }
                    linkedSupplier = new LinkedData(drugCode, drugName, supplierName, contactInfo, location);
                }

                if (linkedSupplier != null) {
                    drugSuppliersList.add(linkedSupplier);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            helper.showError("Error", "Error viewing linked drugs and suppliers");
        }
        BubbleSort.sortSuppliers(drugSuppliersList);
        return drugSuppliersList;
    }
}
