package util;

/**
 * @author tizi
 */
public class GetMaxIndexFromArrUtil {
    public static int getMaxIndex(float[] arr) {
        float maxValue = Float.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > maxValue) {
                maxValue = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
