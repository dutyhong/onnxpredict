package onnx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputDataGenerator {
    private static TokenDictLoad bertTokenDictLoad = null;
    private static TokenDictLoad spanBertTokenDictLoad = null;

    static {
        bertTokenDictLoad = new TokenDictLoad("bert");
        spanBertTokenDictLoad = new TokenDictLoad("spanbert");
    }

    /**
     * 生成句子的模型输入数据inputids attentionsmask toketypeids
     *
     * @param sentence
     * @param maxSeqLen
     * @return
     */
    public static List<long[][]> oneSentenceGenerate(String sentence, int maxSeqLen, String modelName) {

        sentence = sentence.toLowerCase();
        String[] oriTokens = sentence.split(" ");
        List<String> oriTokensNew = new ArrayList<>();
        oriTokensNew.addAll(Arrays.asList(oriTokens));
        // attention_masks
        long[] attentionMasks = new long[maxSeqLen];
        long[] tokenTypeIds = new long[maxSeqLen];
        if (oriTokens.length < maxSeqLen - 2) {
            int diffLen = maxSeqLen - 2 - oriTokens.length;
            for (int i = 0; i < diffLen; i++) {
                oriTokensNew.add("[PAD]");
            }
            for (int i = 0; i < oriTokens.length + 2; i++) {
                attentionMasks[i] = 1;
                tokenTypeIds[i] = 0;
            }
            for (int i = 0; i < maxSeqLen - oriTokens.length - 2; i++) {
                attentionMasks[i + oriTokens.length + 2] = 0;
                tokenTypeIds[i + oriTokens.length + 2] = 0;
            }
            oriTokensNew.add(0, "[CLS]");
            oriTokensNew.add("[SEP]");
        } else {
            oriTokensNew = oriTokensNew.subList(0, maxSeqLen - 2);
            oriTokensNew.add(0, "[CLS]");
            oriTokensNew.add("[SEP]");
            for (int i = 0; i < maxSeqLen; i++) {
                attentionMasks[i] = 1;
                tokenTypeIds[i] = 0;
            }
        }
        // 将tokens转换为ids
        List<Integer> ids = new ArrayList<>();
        if ("bert".equals(modelName))
            ids = bertTokenDictLoad.encode(oriTokensNew);
        if ("spanbert".equals(modelName))
            ids = spanBertTokenDictLoad.encode(oriTokensNew);
        long[][] inputIds = new long[1][maxSeqLen];

        long[] inputIdsTmp = new long[maxSeqLen]; //new long[] {101,34, 3012,6540, 2021,4037,0,102};
        for (int i = 0; i < inputIdsTmp.length; i++) {
            inputIdsTmp[i] = ids.get(i);
        }
        inputIds[0] = inputIdsTmp;

        long[][] batchAttentionMasks = new long[1][maxSeqLen];
//        long[] attention_masks_tmp = new long[]{1,1,1,1,1,1,0,0};
        batchAttentionMasks[0] = attentionMasks;

        long[][] batchTokenTypeIds = new long[1][maxSeqLen];
//        long[] token_type_ids_tmp = new long[]{0,0,0,0,0,0,0,0};
        batchTokenTypeIds[0] = tokenTypeIds;


        long[][] batchSentSlotsIds = new long[1][maxSeqLen];
        long[][] batchEndSlotsIds = new long[1][maxSeqLen];
        List<long[][]> inputData = new ArrayList<>();
        inputData.add(inputIds);
        inputData.add(batchAttentionMasks);
        inputData.add(batchTokenTypeIds);
        inputData.add(batchSentSlotsIds);
        inputData.add(batchEndSlotsIds);
        return inputData;
    }

    /**
     * 多个句子转换
     *
     * @param sentences
     * @param maxSeqlen
     * @return
     */
    public static List<List<long[][]>> batchSentenceGenerate(List<String> sentences, int maxSeqlen, String modelName) {
        List<List<long[][]>> batchInputData = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            List<long[][]> tmpInput = oneSentenceGenerate(sentences.get(i), maxSeqlen, modelName);
            batchInputData.add(tmpInput);
        }
        return batchInputData;
    }

    public static void main(String[] args) {
        String[] aa = {"search adida woman", "wireless bluetooth speaker bluetooth speaker"};
        List<String> bb = Arrays.asList(aa);
        int maxSeqLen = 8;
        List<List<long[][]>> batchOutput = InputDataGenerator.batchSentenceGenerate(bb, maxSeqLen, "bert");
        System.out.println("dddd");
    }
}
