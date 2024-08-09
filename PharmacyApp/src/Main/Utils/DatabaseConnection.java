package Main.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "pharmacy_db";
    private static final String USER = "root";
    private static final String PASSWORD = "DAydNNya";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement statement = connection.createStatement();

        // Create database if it does not exist
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
        statement.executeUpdate(createDatabaseSQL);

        // Connect to the new or existing database
        connection = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
        statement = connection.createStatement();

        // Create tables if they do not exist
        createTables(statement);

        // Check if the Drugs table is empty and insert sample data if it is
        if (isDrugsTableEmpty(connection)) {
            insertSampleData(connection);
            insertSalesData(connection);
            linkSuppliersToDrugs(connection);
        }


        return connection;
    }

    private static void createTables(Statement statement) throws SQLException {
        String createDrugsTableSQL = "CREATE TABLE IF NOT EXISTS Drugs ("
                + "drugCode VARCHAR(20) PRIMARY KEY ,"
                + "name VARCHAR(255) NOT NULL,"
                + "category VARCHAR(50) ,"
                + "price DECIMAL(10, 2) NOT NULL,"
                + "stockQuantity INT NOT NULL"
                + ")";
        statement.executeUpdate(createDrugsTableSQL);

        String createSuppliersTableSQL = "CREATE TABLE IF NOT EXISTS Suppliers ("
                + "supplierId INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "location VARCHAR(100)," +
                "contactInfo VARCHAR(100)"
                + ")";
        statement.executeUpdate(createSuppliersTableSQL);

        String createCustomersTableSQL = "CREATE TABLE IF NOT EXISTS Customers ("
                + "customerId INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "contactInfo VARCHAR(100)"
                + ")";
        statement.executeUpdate(createCustomersTableSQL);

        String createPurchaseTable = "CREATE TABLE IF NOT EXISTS Purchases (" +
                "  purchaseId INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
                "  drugCode VARCHAR(20)," +
                "  quantity INT NOT NULL," +
                "  totalAmount DECIMAL(10, 2) NOT NULL," +
                "  dateTime TIMESTAMP NOT NULL," +
                "  customerId INT," +
                "  FOREIGN KEY (customerId) REFERENCES Customers(customerId)," +
                "  FOREIGN KEY (drugCode) REFERENCES Drugs(drugCode)" +
                "  )";
        statement.executeUpdate(createPurchaseTable);

        String createDrugSuppliersTableSQL = "CREATE TABLE IF NOT EXISTS DrugSuppliers ("
                + "drugCode varchar(20),"
                + "supplierId INT,"
                + "FOREIGN KEY (drugCode) REFERENCES Drugs(drugCode),"
                + "FOREIGN KEY (supplierId) REFERENCES Suppliers(supplierId)"
                + ")";
        statement.executeUpdate(createDrugSuppliersTableSQL);
    }

    private static boolean isDrugsTableEmpty(Connection connection) throws SQLException {
        String checkEmptySQL = "SELECT COUNT(*) FROM Drugs";
        try (PreparedStatement pstmt = connection.prepareStatement(checkEmptySQL);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return false;
        }
    }

    private static void insertSampleData(Connection connection) throws SQLException {
        String insertDrugsSQL = "INSERT INTO Drugs (drugCode, name, category, price, stockQuantity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertDrugsSQL)) {
            pstmt.setString(1, "DR001");
            pstmt.setString(2, "Aspirin");
            pstmt.setString(3, "Pain Reliever");
            pstmt.setBigDecimal(4, new java.math.BigDecimal("5.99"));
            pstmt.setInt(5, 100);
            pstmt.executeUpdate();

            pstmt.setString(1, "DR002");
            pstmt.setString(2, "Ibuprofen");
            pstmt.setString(3, "Anti-inflammatory");
            pstmt.setBigDecimal(4, new java.math.BigDecimal("8.49"));
            pstmt.setInt(5, 150);
            pstmt.executeUpdate();

            pstmt.setString(1, "DR003");
            pstmt.setString(2, "Paracetamol");
            pstmt.setString(3, "Fever Reducer");
            pstmt.setBigDecimal(4, new java.math.BigDecimal("4.29"));
            pstmt.setInt(5, 200);
            pstmt.executeUpdate();
        }

        // Insert sample data into Suppliers
        String insertSuppliersSQL = "INSERT INTO Suppliers (name, location, contactInfo) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSuppliersSQL)) {
            pstmt.setString(1, "HealthCorp");
            pstmt.setString(2, "New York");
            pstmt.setString(3, "contact@healthcorp.com");
            pstmt.executeUpdate();

            pstmt.setString(1, "PharmaPlus");
            pstmt.setString(2, "Los Angeles");
            pstmt.setString(3, "info@pharmaplus.com");
            pstmt.executeUpdate();
        }

        // Insert sample data into Customers
        String insertCustomersSQL = "INSERT INTO Customers (name, contactInfo) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertCustomersSQL)) {
            pstmt.setString(1, "John Doe");
            pstmt.setString(2, "john.doe@example.com");
            pstmt.executeUpdate();

            pstmt.setString(1, "Jane Smith");
            pstmt.setString(2, "jane.smith@example.com");
            pstmt.executeUpdate();
        }
    }

    private static void insertSalesData(Connection connection) throws SQLException {
        String insertPurchasesSQL = "INSERT INTO Purchases (drugCode, quantity, totalAmount, dateTime, customerId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertPurchasesSQL)) {
            pstmt.setString(1, "DR001");
            pstmt.setInt(2, 2);
            pstmt.setBigDecimal(3, new java.math.BigDecimal("11.98"));
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(5, 1);
            pstmt.executeUpdate();

            pstmt.setString(1, "DR002");
            pstmt.setInt(2, 1);
            pstmt.setBigDecimal(3, new java.math.BigDecimal("8.49"));
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(5, 2);
            pstmt.executeUpdate();

            pstmt.setString(1, "DR003");
            pstmt.setInt(2, 5);
            pstmt.setBigDecimal(3, new java.math.BigDecimal("21.45"));
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(5, 1);
            pstmt.executeUpdate();
        }
    }

    private static void linkSuppliersToDrugs(Connection connection) throws SQLException {
        String linkSuppliersSQL = "INSERT INTO DrugSuppliers (drugCode, supplierId) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(linkSuppliersSQL)) {
            pstmt.setString(1, "DR001");
            pstmt.setInt(2, 1); // Linking to HealthCorp
            pstmt.executeUpdate();

            pstmt.setString(1, "DR002");
            pstmt.setInt(2, 2); // Linking to PharmaPlus
            pstmt.executeUpdate();

            pstmt.setString(1, "DR003");
            pstmt.setInt(2, 1); // Linking to HealthCorp
            pstmt.executeUpdate();
        }
    }
}
