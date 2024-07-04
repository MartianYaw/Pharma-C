package Main;

import java.util.LinkedList;
import java.util.List;

/**
 * The Drug class represents a pharmaceutical drug with attributes such as
 * drug code, name, category, price, stock quantity, suppliers, and purchase history.
 */
public class Drug {
    private final String drugCode;
    private final String name;
    private final String category;
    private final double price;
    private final int stockQuantity;
    private final List<Supplier> suppliers;
    private final LinkedList<Purchase> purchaseHistory;

    /**
     * Constructs a new Drug with the specified details.
     *
     * @param drugCode      The unique code for the drug.
     * @param name          The name of the drug.
     * @param category      The category to which the drug belongs.
     * @param price         The price of the drug.
     * @param stockQuantity The quantity of the drug in stock.
     */
    public Drug(String drugCode, String name, String category, double price, int stockQuantity) {
        this.drugCode = drugCode;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.suppliers = new LinkedList<>();
        this.purchaseHistory = new LinkedList<>();
    }

    /**
     * Returns the unique code of the drug.
     *
     * @return The drug code.
     */
    public String getDrugCode() {
        return drugCode;
    }

    /**
     * Returns the name of the drug.
     *
     * @return The name of the drug.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the category of the drug.
     *
     * @return The category of the drug.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the price of the drug.
     *
     * @return The price of the drug.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the current stock quantity of the drug.
     *
     * @return The stock quantity of the drug.
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Returns the list of suppliers for the drug.
     *
     * @return A list of suppliers.
     */
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    /**
     * Returns the purchase history of the drug.
     *
     * @return A linked list of purchases.
     */
    public LinkedList<Purchase> getPurchaseHistory() {
        return purchaseHistory;
    }


    /**
     * Adds a supplier to the list of suppliers for the drug.
     *
     * @param supplier The supplier to be added.
     */
    public void addSupplier(Supplier supplier) {
        this.suppliers.add(supplier);
    }
}
