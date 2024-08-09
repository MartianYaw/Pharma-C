package Main.Models;

public class LinkedData {
    private final String drugCode;
    private final String drugName;
    private final String supplierName;
    private final String contact;
    private final String location;

    public LinkedData(String drugCode, String drugName, String supplierName, String contact, String location) {
        this.drugCode = drugCode;
        this.drugName = drugName;
        this.supplierName = supplierName;
        this.contact = contact;
        this.location = location;
    }

    // Getters
    public String getDrugCode() {
        return drugCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getContact() {
        return contact;
    }

    public String getLocation() {
        return location;
    }
}
