package biz;

import edu.stanford.nlp.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tizi,muzhong
 */
public class ModelPredictResultModify {
    public RuleDataLoad ruleDataLoad = null;

    public ModelPredictResultModify() {
        ruleDataLoad = new RuleDataLoad();
    }

    public static void main(String[] args) {
        ModelPredictResultModify modelPredictResultModify = new ModelPredictResultModify();
        String[] aa = {"nike n95", "penis enlargement oil"};
        String[] bb = {"S_brand S_category", "B_category E_category S_category"};
        String res = modelPredictResultModify.preModify(aa[0], bb[0]);
        res = modelPredictResultModify.styleWordsModify(aa[0], res);
        res = modelPredictResultModify.typeWordsModify(aa[0], res);
        res = modelPredictResultModify.elementWordsModify(aa[0], res);
        res = modelPredictResultModify.beCategoryWordsModify(aa[0], res);
        res = modelPredictResultModify.catemodifierWordsModify(aa[0], res);
        System.out.println(res);
        System.out.println("dddd");
    }
    //风格修正

    //开始规则修正
    //先对品牌 功能功效 ip 人群进行修正
    private String preModify(String inputSent, String predTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = predTags.split(" ");
        String[] tmpCategoryNames = {"brand", "ip", "population", "function"};
        Map<String, String> beforePredictMatchDict = ruleDataLoad.beforePredictMatchDict;
        List<String> categoryNames = Arrays.asList(tmpCategoryNames);
        int len = tags.length;
        int[] modifiedFlags = new int[len];
        for (int i = 0; i < len; i++) {
            modifiedFlags[i] = 0;
        }
        for (int i = 0; i < len; i++) {
            if ("O".equals(tags[i]))
                continue;
            String cateName = tags[i].split("_")[1];
            if (categoryNames.contains(cateName)) {
                tags[i] = "O";
            }
        }
        //句子长度为1
        if (len == 1) {
            if (beforePredictMatchDict.containsKey(words[0])) {
                String tmpCateName = beforePredictMatchDict.get(words[0]);
                tags[0] = "S_" + tmpCateName;
            }
            return StringUtils.join(tags, " ");
        }
        if (len == 2) {
            if (beforePredictMatchDict.containsKey(inputSent)) {
                String tmpCateName = beforePredictMatchDict.get(inputSent);
                tags[0] = "B_" + tmpCateName;
                tags[1] = "E_" + tmpCateName;
                return StringUtils.join(tags, " ");
            }
            for (int i = 0; i < len; i++) {
                if (beforePredictMatchDict.containsKey(words[i])) {
                    String tmpCateName = beforePredictMatchDict.get(words[i]);
                    tags[i] = "S_" + tmpCateName;
                }
            }
            return StringUtils.join(tags, " ");
        }
        if (len == 3) {
            if (beforePredictMatchDict.containsKey(inputSent)) {
                String tmpCateName = beforePredictMatchDict.get(inputSent);
                tags[0] = "B_" + tmpCateName;
                tags[1] = "I_" + tmpCateName;
                tags[2] = "E_" + tmpCateName;
                return StringUtils.join(tags, " ");
            }
            for (int i = 0; i < len - 1; i++) {
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
                if (beforePredictMatchDict.containsKey(tmpWords)) {
                    String tmpCateName = beforePredictMatchDict.get(tmpWords);
                    tags[i] = "B_" + tmpCateName;
                    tags[i + 1] = "E_" + tmpCateName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
            for (int i = 0; i < len; i++) {
                String tmpWords = words[i];
                if (modifiedFlags[i] == 1)
                    continue;
                if (beforePredictMatchDict.containsKey(tmpWords)) {
                    String tmpCateName = beforePredictMatchDict.get(tmpWords);
                    tags[i] = "S_" + tmpCateName;
                }
            }
            return StringUtils.join(tags, " ");
        }
        if (len >= 4) {
            for (int i = 0; i < len - 3; i++) {
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 4), " ");
                if (beforePredictMatchDict.containsKey(tmpWords)) {
                    String tmpCateName = beforePredictMatchDict.get(tmpWords);
                    tags[i] = "B_" + tmpCateName;
                    tags[i + 1] = "I_" + tmpCateName;
                    tags[i + 2] = "I_" + tmpCateName;
                    tags[i + 3] = "E_" + tmpCateName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                    modifiedFlags[i + 2] = 1;
                    modifiedFlags[i + 3] = 1;
                }
            }
            for (int i = 0; i < len - 2; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 3), " ");
                if (beforePredictMatchDict.containsKey(tmpWords)) {
                    String tmpCateName = beforePredictMatchDict.get(tmpWords);
                    tags[i] = "B_" + tmpCateName;
                    tags[i + 1] = "I_" + tmpCateName;
                    tags[i + 2] = "E_" + tmpCateName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                    modifiedFlags[i + 2] = 1;
                }
            }
            for (int i = 0; i < len - 1; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
                if (beforePredictMatchDict.containsKey(tmpWords)) {
                    String tmpCateName = beforePredictMatchDict.get(tmpWords);
                    tags[i] = "B_" + tmpCateName;
                    tags[i + 1] = "E_" + tmpCateName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
            for (int i = 0; i < len; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                String tmpWords = words[i];
                if (beforePredictMatchDict.containsKey(tmpWords)) {
                    String tmpCateName = beforePredictMatchDict.get(tmpWords);
                    tags[i] = "S_" + tmpCateName;
                }
            }
            return StringUtils.join(tags, " ");
        }

        return StringUtils.join(tags, " ");
    }

    /***
     * 风格实体修正
     * @param inputSent
     * @param modifiedTags
     * @return
     */
    private String styleWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        Set<String> styleWords = ruleDataLoad.styleWords;
        int len = tags.length;
        String[] conditionTags1 = {"S_catemodifier", "O", "S_modifier"};
        String[] conditionTags2 = {"S_category", "B_category"};
        List<String> conditionsTags1List = Arrays.asList(conditionTags1);
        List<String> conditionsTags2List = Arrays.asList(conditionTags2);
        for (int i = 0; i < len - 2; i++) {
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
            if (styleWords.contains(tmpWords) && "S_category".equals(tags[i + 2])) {
                tags[i] = "B_style";
                tags[i + 1] = "E_style";
            }
        }
        for (int i = 0; i < len - 1; i++) {
//            String tmpWords = StringUtils.join(wordsList.subList(i, i+2), " ");
            if ("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1]) && styleWords.contains(words[i])) {
                tags[i] = "S_style";
                tags[i + 1] = "S_category";
            }
            if (conditionsTags1List.contains(tags[i]) && conditionsTags2List.contains(tags[i + 1]) &&
                    styleWords.contains(words[i])) {
                tags[i] = "S_style";
            }
        }
        for (int i = 0; i < len - 2; i++) {
            if ("S_population".equals(tags[i + 1]) && "S_category".equals(tags[i + 1]) && styleWords.contains(words[i]))
                tags[i] = "S_style";
        }
        return StringUtils.join(tags, " ");
    }

    /***
     * 款式元素修正
     * @param inputSent
     * @param modifiedTags
     * @return
     */
    private String elementWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        Set<String> elementWords = ruleDataLoad.elementWords;
        int len = tags.length;
        String[] conditionTags1 = {"S_catemodifier", "O", "S_modifier"};
        String[] conditionTags2 = {"S_category", "B_category"};
        List<String> conditionsTags1List = Arrays.asList(conditionTags1);
        List<String> conditionsTags2List = Arrays.asList(conditionTags2);
        for (int i = 0; i < len - 2; i++) {
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
            if (elementWords.contains(tmpWords) && "S_category".equals(tags[i + 2])) {
                tags[i] = "B_element";
                tags[i + 1] = "E_element";
            }
        }
        for (int i = 0; i < len - 1; i++) {
//            String tmpWords = StringUtils.join(wordsList.subList(i, i+2), " ");
            if ("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1]) && elementWords.contains(words[i])) {
                tags[i] = "S_element";
                tags[i + 1] = "S_category";
            }
            if (conditionsTags1List.contains(tags[i]) && conditionsTags2List.contains(tags[i + 1]) &&
                    elementWords.contains(words[i])) {
                tags[i] = "S_element";
            }
        }
        for (int i = 0; i < len - 2; i++) {
            if ("S_population".equals(tags[i + 1]) && "S_category".equals(tags[i + 2]) && elementWords.contains(words[i]))
                tags[i] = "S_element";
        }
        return StringUtils.join(tags, " ");
    }

    //型号词修正
    private String typeWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        int len = words.length;
        int[] modifiedFlags = new int[len];

        for (int i = 0; i < len - 1; i++) {
            if (tags[i].contains("brand") && !tags[i + 1].contains("type") && IsTypeWord.digitWord(words[i + 1])) {
                tags[i + 1] = "S_type";
            }
        }
        if (len == 2) {
            for (int i = 0; i < len - 1; i++) {
                if (IsTypeWord.isTypeWords(wordsList, ruleDataLoad) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]))) {
                    tags[i] = "B_type";
                    tags[i + 1] = "E_type";
                }
            }
        }
        if (len == 3) {
            for (int i = 0; i < len - 2; i++) {
                if (modifiedFlags[i] == 1)
                    continue;

                List<String> subList = wordsList.subList(i, i + 3);
                if (IsTypeWord.isTypeWords(subList, ruleDataLoad) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]) || "O".equals(tags[i + 2]))) {
                    tags[i] = "B_type";
                    tags[i + 1] = "I_type";
                    tags[i + 2] = "E_type";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                    modifiedFlags[i + 2] = 1;
                }

            }
            for (int i = 0; i < len - 1; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                List<String> subList = wordsList.subList(i, i + 2);
                if (IsTypeWord.isTypeWords(subList, ruleDataLoad) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]))) {
                    tags[i] = "B_type";
                    tags[i + 1] = "E_type";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }

        }
        if (len >= 4) {
            for (int i = 0; i < len - 3; i++) {
                if (modifiedFlags[i] == 1)
                    continue;

                List<String> subList = wordsList.subList(i, i + 4);
                if (IsTypeWord.isTypeWords(subList, ruleDataLoad)) {
                    tags[i] = "B_type";
                    tags[i + 1] = "I_type";
                    tags[i + 2] = "I_type";
                    tags[i + 3] = "E_type";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                    modifiedFlags[i + 2] = 1;
                    modifiedFlags[i + 3] = 1;
                }

            }
            for (int i = 0; i < len - 2; i++) {
                if (modifiedFlags[i] == 1)
                    continue;

                List<String> subList = wordsList.subList(i, i + 3);
                if (IsTypeWord.isTypeWords(subList, ruleDataLoad) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]) || "O".equals(tags[i + 2]))) {
                    tags[i] = "B_type";
                    tags[i + 1] = "I_type";
                    tags[i + 2] = "E_type";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                    modifiedFlags[i + 2] = 1;
                }

            }
            for (int i = 0; i < len - 1; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                List<String> subList = wordsList.subList(i, i + 2);
                if (IsTypeWord.isTypeWords(subList, ruleDataLoad) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]))) {
                    tags[i] = "B_type";
                    tags[i + 1] = "E_type";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
        }
        return StringUtils.join(tags, " ");
    }

    //品类词修正
    private String beCategoryWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        int len = words.length;
        int[] modifiedFlags = new int[len];
        Set<String> categoryWords = ruleDataLoad.beCategoryWords;
        Set<String> catemodifierWords = ruleDataLoad.catemodifierWords;
        String[] conditionTags = {"S_catemodifier", "O", "S_modifier"};
        List<String> conditionTagsList = Arrays.asList(conditionTags);
        for (int i = 0; i < len - 2; i++) {
            if (modifiedFlags[i] == 1)
                continue;
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 3), " ");
            if (categoryWords.contains(tmpWords)) {
                tags[i] = "B_category";
                tags[i + 1] = "I_category";
                tags[i + 2] = "E_category";
                modifiedFlags[i] = 1;
                modifiedFlags[i + 1] = 1;
                modifiedFlags[i + 2] = 1;
            }
        }
        for (int i = 0; i < len - 1; i++) {
            if (modifiedFlags[i] == 1)
                continue;
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
            if (categoryWords.contains(tmpWords)) //&& (!tags[i].contains("category")))
            {
                tags[i] = "B_category";
                tags[i + 1] = "E_category";
                modifiedFlags[i] = 1;
                modifiedFlags[i + 1] = 1;
            }
        }
        for (int i = 0; i < len - 1; i++) {
            if ("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1])) {
                if (modifiedFlags[i] == 1)
                    continue;
                if (catemodifierWords.contains(words[i])) {
                    tags[i] = "S_catemodifier";
                    tags[i + 1] = "S_category";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
        }

        return StringUtils.join(tags, " ");
    }

    //品类修饰词修正
    private String catemodifierWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        int len = words.length;
        Set<String> catemodifierWords = ruleDataLoad.catemodifierWords;
        String[] conditionTags = {"S_catemodifier", "O", "S_modifier"};
        List<String> conditionTagsList = Arrays.asList(conditionTags);
        for (int i = 0; i < len - 1; i++) {
            if (("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1])) ||
                    (conditionTagsList.contains(tags[i]) && "S_category".equals(tags[i + 1]))) {
                if (catemodifierWords.contains(words[i])) {
                    tags[i] = "S_catemodifier";
                    tags[i + 1] = "S_category";
                }
            }
        }
        return StringUtils.join(tags, " ");
    }

    //modifystage
    public String modifyStage(String inputSent, String modifiedTags) {
        //品牌 ip 人群 功能功效
        String res = preModify(inputSent, modifiedTags);

        TagPosModify tagPosModify = new TagPosModify();
        res = tagPosModify.tagPosModifyModelOutput(inputSent, res);
        res = typeWordsModify(inputSent, res);
        res = styleWordsModify(inputSent, res);
        res = elementWordsModify(inputSent, res);
        res = beCategoryWordsModify(inputSent, res);

//        res = catemodifierWordsModify(inputSent, res);
        return res;
    }

}
