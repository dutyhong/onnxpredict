package util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tizi
 */
public class SpanBertExtractEntity {
    public static List<int[]> entityExtractFromSpanBertOutputs(float[][] startLogits, float[][] endLogits, int maxSeqLen) {
        int[] argStartMaxIndice = new int[maxSeqLen - 2];
        int[] argEndMaxIndice = new int[maxSeqLen - 2];
        for (int i = 1; i < maxSeqLen - 1; i++) {
            float[] startLogit = startLogits[i];
            int tmpStartMax = GetMaxIndexFromArrUtil.getMaxIndex(startLogit);
            argStartMaxIndice[i - 1] = tmpStartMax;
            float[] endLogit = endLogits[i];
            int tmpEndMax = GetMaxIndexFromArrUtil.getMaxIndex(endLogit);
            argEndMaxIndice[i - 1] = tmpEndMax;

        }
        List<int[]> entityTerms = new ArrayList<>();
        for (int i = 0; i < argStartMaxIndice.length; i++) {
            if (argStartMaxIndice[i] == 0) {
                continue;
            }
            for (int j = i; j < argEndMaxIndice.length; j++) {
                if (argStartMaxIndice[i] == argEndMaxIndice[j]) {
                    int[] tmpTerm = new int[3];
                    tmpTerm[0] = i;
                    tmpTerm[1] = j;
                    tmpTerm[2] = argStartMaxIndice[i];
                    entityTerms.add(tmpTerm);
                    break;
                }

            }
        }
        return entityTerms;

    }

    public static void main(String[] args) {
        System.out.println("dddd");
    }
}
