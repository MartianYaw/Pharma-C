package Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinearSearch {

    // Method to search for a drug by name using linear search with Iterator
    public static Drug linearSearchByName(List<Drug> drugs, String name) {
        Iterator<Drug> iterator = drugs.iterator();
        while (iterator.hasNext()) {
            Drug drug = iterator.next();
            if (drug.getName().equalsIgnoreCase(name)) {
                return drug;
            }
        }
        return null;
    }

    // Method to search for a drug by drugCode using linear search with Iterator
    public static Drug linearSearchByCode(List<Drug> drugs, String drugCode) {
        Iterator<Drug> iterator = drugs.iterator();
        while (iterator.hasNext()) {
            Drug drug = iterator.next();
            if (drug.getDrugCode().equals(drugCode)) {
                return drug;
            }
        }
        return null;
    }

    // Method to search suppliers by location using linear search with Iterator
    public static List<Supplier> searchSuppliersByLocation(List<Supplier> suppliers,  String location) {
        List<Supplier> suppliersList = new ArrayList<>();
        Iterator<Supplier> iterator = suppliers.iterator();
        while (iterator.hasNext()) {
            Supplier supplier = iterator.next();
            if (supplier.getLocation().equalsIgnoreCase(location)) {
                suppliersList.add(supplier);
            }
        }
        return suppliersList;
    }
}
