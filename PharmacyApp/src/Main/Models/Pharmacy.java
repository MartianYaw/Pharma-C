package Main.Models;

import Main.Services.LoadFromDb;

import java.time.LocalDateTime;
import java.util.*;

public class Pharmacy {
    private Map<String, Drug> drugs;
    private List<LinkedData> suppliers;
    private List<Customers> customers;
    private Map<LocalDateTime, Purchase> purchases;

    private final LoadFromDb db;

    public Pharmacy() {
        this.db = new LoadFromDb();
        this.drugs = new HashMap<>();
        this.suppliers = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.purchases = new HashMap<>();
    }

    public Map<String, Drug> getDrugs() {
        this.drugs = db.viewAllDrugs();
        return drugs;
    }

    public List<LinkedData> getSuppliers() {
        this.suppliers = db.viewLinkedDrugAndSuppliers();
        return suppliers;
    }

    public List<Customers> getCustomers() {
        this.customers = db.viewAllCustomers();
        return customers;
    }

    public Map<LocalDateTime, Purchase> getPurchases() {
        this.purchases = db.getSalesReport();
        return purchases;
    }

    public void addDrug(Drug drug) {
        this.drugs.put(drug.getDrugCode(), drug);
    }

    public void removeDrug(String drugCode) {
        this.drugs.remove(drugCode);
    }

    public void addSupplier(LinkedData supplier) {
        this.suppliers.add(supplier);
    }

    public void addCustomer(Customers customer) {
        this.customers.add(customer);
    }

    public void addPurchase(Purchase purchase) {
        this.purchases.put(purchase.getDateTime(), purchase);
    }
}
