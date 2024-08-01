package Main;

import java.sql.Connection;
import java.sql.DriverManager;
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

        return connection;
    }
}
