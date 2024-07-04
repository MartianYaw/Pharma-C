package Main;

import java.util.List;

public class MergeSort {
    public static void mergeSort(List<Drug> drugs, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(drugs, left, mid);
            mergeSort(drugs, mid + 1, right);
            merge(drugs, left, mid, right);
        }
    }

    private static void merge(List<Drug> drugs, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Drug[] leftArray = new Drug[n1];
        Drug[] rightArray = new Drug[n2];

        for (int i = 0; i < n1; ++i)
            leftArray[i] = drugs.get(left + i);
        for (int j = 0; j < n2; ++j)
            rightArray[j] = drugs.get(mid + 1 + j);

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i].getName().compareTo(rightArray[j].getName()) <= 0) {
                drugs.set(k, leftArray[i]);
                i++;
            } else {
                drugs.set(k, rightArray[j]);
                j++;
            }
            k++;
        }

        while (i < n1) {
            drugs.set(k, leftArray[i]);
            i++;
            k++;
        }

        while (j < n2) {
            drugs.set(k, rightArray[j]);
            j++;
            k++;
        }
    }
}
