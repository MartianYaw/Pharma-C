package Main;

import java.util.List;

public class MergeSort {
    public static void bubbleSortName(List<Drug> drugs) {
        int n = drugs.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (drugs.get(j).getName().compareTo(drugs.get(j + 1).getName()) > 0) {
                    Drug temp = drugs.get(j);
                    drugs.set(j, drugs.get(j + 1));
                    drugs.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static void bubbleSortCode(List<Drug> drugs) {
        int n = drugs.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (drugs.get(j).getDrugCode().compareTo(drugs.get(j + 1).getDrugCode()) > 0) {
                    Drug temp = drugs.get(j);
                    drugs.set(j, drugs.get(j + 1));
                    drugs.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }


    public static void sortPurchasesByDateTime(List<Purchase> purchases) {
        int n = purchases.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (purchases.get(j).getDateTime().isBefore(purchases.get(j + 1).getDateTime())) {
                    Purchase temp = purchases.get(j);
                    purchases.set(j, purchases.get(j + 1));
                    purchases.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static void sortSuppliers(List<Supplier> suppliers) {
        int n = suppliers.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (suppliers.get(j).getLocation().compareToIgnoreCase(suppliers.get(j + 1).getLocation()) > 0) {
                    Supplier temp = suppliers.get(j);
                    suppliers.set(j, suppliers.get(j + 1));
                    suppliers.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }
}
