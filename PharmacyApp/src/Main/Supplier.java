package Main;

public class Supplier {
    private Long id;

    private String name;

    private String location;

    private String contactInfo;

    public Supplier() {
    }

    public Supplier(String name, String location, String contactInfo) {
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public String toString() {
        return String.format(" %-20s %-20s %-15s", name, location, contactInfo);
    }
}
