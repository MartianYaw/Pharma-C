package Main;

import java.time.LocalDateTime;

public class Purchase {
    private final String drugCode;
    private final int quantity;
    private double totalAmount;
    private final LocalDateTime dateTime;
    private final String buyer;

    public Purchase(String drugCode, int quantity, double totalAmount, LocalDateTime dateTime,
            String buyer) {
        this.drugCode = drugCode;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.dateTime = dateTime;
        this.buyer = buyer;
    }

    public Purchase(String drugCode, int quantity, LocalDateTime dateTime, String buyer) {
        this.drugCode = drugCode;
        this.quantity = quantity;
        this.dateTime = dateTime;
        this.buyer = buyer;
    }


    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDrugCode() {
        return drugCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getBuyer() {
        return buyer;
    }
}
