package Main.Utils;

import Main.Models.Drug;
import Main.Models.LinkedData;
import Main.Models.Purchase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BubbleSort {
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


    public static void sortPurchasesByDateTime(Map<LocalDateTime, Purchase> purchaseMap) {
        List<Purchase> purchases = new ArrayList<>(purchaseMap.values());
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

    public static void sortSuppliers(List<LinkedData> linkedSuppliers) {
        int n = linkedSuppliers.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (linkedSuppliers.get(j).getLocation().compareToIgnoreCase(linkedSuppliers.get(j+1).getLocation()) > 0){
                    LinkedData temp = linkedSuppliers.get(j);
                    linkedSuppliers.set(j, linkedSuppliers.get(j + 1));
                    linkedSuppliers.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

    }

}
