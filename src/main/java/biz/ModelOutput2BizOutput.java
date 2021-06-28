package biz;

import edu.stanford.nlp.util.StringUtils;
import onnx.NerTagLoad;
import util.SpanBertExtractEntity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ModelOutput2BizOutput {
    private static int maxSeqLen = 0;
    private static AgeEntityExtract ageEntityExtract = null;

    private static ModelPredictResultModify modelPredictResultModify = null;
    private static Map<Integer, String> index2Tag = new HashMap<>();

    static {
        InputStream path = NerTagLoad.class.getResourceAsStream("/model_parameter");
        BufferedReader br = new BufferedReader(new InputStreamReader(path));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                String[] columns = line.trim().split(":");
                maxSeqLen = Integer.parseInt(columns[1]);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ageEntityExtract = new AgeEntityExtract();
        modelPredictResultModify = new ModelPredictResultModify();
        NerTagLoad.tagLoad("span_ner_tags");
        index2Tag = NerTagLoad.index2Tag;
    }

    /***
     * 将结果转换为字符串格式
     * @param sents
     * @param startLogits
     * @param endLogits
     * @return
     * @throws Exception
     */
    public static String transfer2String(String sents, float[][] startLogits, float[][] endLogits) throws Exception {

        //先将模型的输出转换为phone case for samsung a50;B_category E_category O S_brand S_type 格式
        sents = sents.replaceAll("[' ']+", " ").replaceAll("\t", " ");
        String[] oneSentResult = modelOutputLogits2Entity(sents, startLogits, endLogits, maxSeqLen, index2Tag);
        List<String> tags = new ArrayList<String>(Arrays.asList(oneSentResult));
        String[] inputWords = sents.split(" ");
        int len = inputWords.length;

        String posTagsStr = StringUtils.join(tags, " ");
        //年龄
        posTagsStr = ageEntityExtract.ageEntityTag(sents, posTagsStr);
        //修正项开始
        posTagsStr = modelPredictResultModify.modifyStage(sents, posTagsStr);
        return posTagsStr;
    }

    /***
     * 将结果转换为list  map格式
     * @param sents
     * @param startLogits
     * @param endLogits
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> transfer2Map(String sents, float[][] startLogits, float[][] endLogits) throws Exception {
        //先将模型的输出转换为phone case for samsung a50;B_category E_category O S_brand S_type 格式
        sents = sents.replaceAll("[' ']+", " ").replaceAll("\t", " ");
        String[] oneSentResult = modelOutputLogits2Entity(sents, startLogits, endLogits, maxSeqLen, index2Tag);
        List<String> tags = new ArrayList<String>(Arrays.asList(oneSentResult));
        String[] inputWords = sents.split(" ");
        int len = inputWords.length;

        String posTagsStr = StringUtils.join(tags, " ");
        //年龄
        posTagsStr = ageEntityExtract.ageEntityTag(sents, posTagsStr);
        //修正项开始
        posTagsStr = modelPredictResultModify.modifyStage(sents, posTagsStr);
        List<String> tokens = new ArrayList<>();
        List<String> nerTags = new ArrayList<>();
        StringBuilder tagPosition = new StringBuilder();
        String[] bb = posTagsStr.split(" ");
        for (int i = 0; i < inputWords.length; i++) {
            tokens.add(inputWords[i]);
            nerTags.add(bb[i]);
            if ("O".equals(bb[i])) {
                tagPosition.append("O");
            } else {
                tagPosition.append(bb[i].split("_")[0]);

            }
        }
        List<Map<String, String>> result = TagSent2TagMap.tagSent2TagMap(tagPosition.toString(), nerTags, tokens);
        return result;
    }

    public static String[] modelOutputLogits2Entity(String sent, float[][] startLogits, float[][] endLogits, int maxSeqLen, Map<Integer, String> index2Tag) {
        List<int[]> entities = new ArrayList<>();
        //entities里面可能会有重叠 如[5,0,2] [5,1,2]
        entities = SpanBertExtractEntity.entityExtractFromSpanBertOutputs(startLogits, endLogits, maxSeqLen);
        for (int i = 0; i < entities.size(); i++) {
            int[] entity = entities.get(i);
            int startId = entity[0];
            int endId = entity[1];
            String tagName = index2Tag.get(entity[2]);
            System.out.println(startId + "," + endId + "," + tagName);
        }
//        Map<Integer, Map<String, String>> results = SpanBertEntity2BizEntity.oneTransfer(batchSents.get(0), entities,index2Tag);
        String[] results = SpanBertEntity2BizEntity.oneTransfer(sent, entities, index2Tag);
//        System.out.println(results.toString());
        return results;
    }

    public static void main(String[] args) {

    }
}
