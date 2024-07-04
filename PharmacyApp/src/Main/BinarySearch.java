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
}
