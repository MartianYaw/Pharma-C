package Main;

public class Customers {
    private final String buyerName;
    private final String contactInfo;
    private final int purchases;

    public Customers(String buyerName, String contactInfo, int purchases) {
        this.buyerName = buyerName;
        this.contactInfo = contactInfo;
        this.purchases = purchases;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public int getPurchases() {
        return purchases;
    }
}
