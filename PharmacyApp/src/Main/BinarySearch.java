package Main;

import java.util.List;

public class BinarySearch {

    public static Drug binarySearchByName(List<Drug> drugs, String name) {
        int left = 0;
        int right = drugs.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int result = name.compareToIgnoreCase(drugs.get(mid).getName());

            if (result == 0) {
                return drugs.get(mid);
            }
            if (result > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    public static Drug binarySearchByCode(List<Drug> drugs, String drugCode) {
        int left = 0;
        int right = drugs.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int result = drugCode.compareTo(drugs.get(mid).getDrugCode());

            if (result == 0) {
                return drugs.get(mid);
            }
            if (result > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    public static List<Supply> searchSuppliersByLocation(List<Drug> drugs, String drugCode, String location) {
        for (Drug drug : drugs) {
            if (drug.getDrugCode().equals(drugCode)) {
                List<Supply> suppliers = drug.getSupplies();
                List<Supply> result = new ArrayList<>();
                for (Supply supplier : suppliers) {
                    if (supplier.getLocation().equalsIgnoreCase(location)) {
                        result.add(supplier);
                    }
                }
                return result;
            }
        }
        return null;
    }

    public static Drug search(List<Drug> drugs, String type, String query) {
        switch (type.toLowerCase()) {
            case "name":
                return binarySearchByName(drugs, query);
            case "code":
                return binarySearchByCode(drugs, query);
            case "supplier":
                String[] parts = query.split(";");
                if (parts.length == 2) {
                    String drugCode = parts[0];
                    String location = parts[1];
                    return searchSuppliersByLocation(drugs, drugCode, location);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + type);
        }
        return null;
    }
}
