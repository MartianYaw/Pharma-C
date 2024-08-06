package Main.Models;

import java.time.LocalDateTime;

public class Purchase {
    private  String drugCode;
    private  int quantity;
    private double totalAmount;
    private  LocalDateTime dateTime;
    private String buyerName;
    private String contactInfo;

    public  Purchase(){

    }



    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDrugCode() {
        return drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getBuyerName() {
        return buyerName;
    }

}
