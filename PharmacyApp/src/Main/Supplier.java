package Main;

public record Supplier(String name, String location, String contactInfo) {
    @Override
    public String toString() {
        return String.format(" %-20s %-20s %-15s", name, location, contactInfo);
    }
}
