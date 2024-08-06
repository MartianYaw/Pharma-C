package Main.Models;

import Main.Services.LoadFromDb;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents the pharmacy system, managing drugs, suppliers, customers, and purchases.
 * Provides methods to retrieve data from the database and manipulate in-memory collections.
 */
public class Pharmacy {
    private Map<String, Drug> drugs;
    private List<LinkedData> suppliers;
    private List<Customers> customers;
    private Map<LocalDateTime, Purchase> purchases;

    private final LoadFromDb db;

    /**
     * Constructs a new Pharmacy object and initializes the database connection.
     */
    public Pharmacy() {
        this.db = new LoadFromDb();
        this.drugs = new HashMap<>();
        this.suppliers = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.purchases = new HashMap<>();
    }

    /**
     * Retrieves the map of all drugs from the database.
     *
     * @return A map where the key is the drug code and the value is the Drug object.
     */
    public Map<String, Drug> getDrugs() {
        this.drugs = db.viewAllDrugs();
        return drugs;
    }

    /**
     * Retrieves the list of all linked drugs and their suppliers from the database.
     *
     * @return A list of LinkedData objects representing drugs and their associated suppliers.
     */
    public List<LinkedData> getSuppliers() {
        this.suppliers = db.viewLinkedDrugAndSuppliers();
        return suppliers;
    }

    /**
     * Retrieves the list of all customers from the database.
     *
     * @return A list of Customers objects representing all customers.
     */
    public List<Customers> getCustomers() {
        this.customers = db.viewAllCustomers();
        return customers;
    }

    /**
     * Retrieves the map of all purchases from the database.
     *
     * @return A map where the key is the date and time of the purchase and the value is the Purchase object.
     */
    public Map<LocalDateTime, Purchase> getPurchases() {
        this.purchases = db.getSalesReport();
        return purchases;
    }

    /**
     * Adds a new drug to the in-memory collection of drugs.
     *
     * @param drug The Drug object to be added.
     */
    public void addDrug(Drug drug) {
        this.drugs.put(drug.getDrugCode(), drug);
    }

    /**
     * Removes a drug from the in-memory collection of drugs.
     *
     * @param drugCode The code of the drug to be removed.
     */
    public void removeDrug(String drugCode) {
        this.drugs.remove(drugCode);
    }

    /**
     * Adds a new supplier to the in-memory collection of suppliers.
     *
     * @param supplier The LinkedData object representing the supplier.
     */
    public void addSupplier(LinkedData supplier) {
        this.suppliers.add(supplier);
    }

    /**
     * Adds a new customer to the in-memory collection of customers.
     *
     * @param customer The Customers object to be added.
     */
    public void addCustomer(Customers customer) {
        this.customers.add(customer);
    }

    /**
     * Adds a new purchase to the in-memory collection of purchases.
     *
     * @param purchase The Purchase object to be added.
     */
    public void addPurchase(Purchase purchase) {
        this.purchases.put(purchase.getDateTime(), purchase);
    }
}
