package Main.Utils;

import Main.Models.Drug;
import Main.Models.LinkedData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinearSearch {

    // Method to search for a drug by name using linear search with Iterator
    public static List<Drug> linearSearchByName(List<Drug> drugs, String name) {
        List<Drug> drugList = new ArrayList<>();

        if (drugs != null && name != null) {
            Iterator<Drug> iterator = drugs.iterator();
            while (iterator.hasNext()) {
                Drug drug = iterator.next();
                if (drug.getName() != null && drug.getName().equalsIgnoreCase(name)) {
                    drugList.add(drug);
                }
            }
        }
        return drugList;
    }

    // Method to search for a drug by drugCode using linear search with Iterator
    public static List<Drug> linearSearchByCode(List<Drug> drugs, String drugCode) {
        List<Drug> drugList = new ArrayList<>();

        if (drugs != null && drugCode != null) {
            Iterator<Drug> iterator = drugs.iterator();
            while (iterator.hasNext()) {
                Drug drug = iterator.next();
                if (drug.getDrugCode() != null && drug.getDrugCode().equals(drugCode)) {
                    drugList.add(drug);
                }
            }
        }

        return drugList;
    }

    // Method to search drug list by location using linear search with Iterator
    public static List<LinkedData> searchSuppliersByLocation(List<LinkedData> drugList, String location) {
        List<LinkedData> filtered = new ArrayList<>();
        Iterator<LinkedData> iterator = drugList.iterator();
        while (iterator.hasNext()) {
            LinkedData linkedSupplier = iterator.next();
            if (linkedSupplier.getLocation().equalsIgnoreCase(location)) {
                filtered.add(linkedSupplier);
            }
        }
        return filtered;
    }
}
