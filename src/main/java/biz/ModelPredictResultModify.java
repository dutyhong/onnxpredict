package biz;

import edu.stanford.nlp.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author tizi,muzhong
 */
public class ModelPredictResultModify {

    /**
     * 品牌词规则修正
     * @param inputSent
     * @param predTags
     * @return
     */
    private String brandWordModify(String inputSent, String predTags) {
        return preModify(inputSent, predTags, "brand");
    }

    /**
     * ip词规则修正
     * @param inputSent
     * @param predTags
     * @return
     */
    private String ipWordModify(String inputSent, String predTags) {
        return preModify(inputSent, predTags, "ip");
    }

    /**
     * 人群词规则修正
     * @param inputSent
     * @param predTags
     * @return
     */
    private String populationWordModify(String inputSent, String predTags) {
        return preModify(inputSent, predTags, "population");
    }

    /**
     * 功能词规则修正
     * @param inputSent
     * @param predTags
     * @return
     */
    private String functionWordModify(String inputSent, String predTags) {
        return preModify(inputSent, predTags, "function");
    }

    /**
     * 修正特定类型
     * @param inputSent
     * @param predTags
     * @param typeName 目前支持的是  "brand", "ip", "population", "function"
     * @return
     */
    private String preModify(String inputSent, String predTags, String typeName) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = predTags.split(" ");
        int len = tags.length;
        int[] modifiedFlags = new int[len];
        for (int i = 0; i < len; i++) {
            modifiedFlags[i] = 0;
        }
        for (int i = 0; i < len; i++) {
            if ("O".equals(tags[i]))
                continue;
            String cateName = tags[i].split("_")[1];
            if (typeName.equalsIgnoreCase(cateName)) {
                tags[i] = "O";
            }
        }
        //句子长度为1
        if (len == 1) {
            if (RuleDataLoad.checkWordType(words[0], typeName)) {
                tags[0] = "S_" + typeName;
            }
            return StringUtils.join(tags, " ");
        }
        if (len == 2) {
            if (RuleDataLoad.checkWordType(inputSent, typeName)) {
                tags[0] = "B_" + typeName;
                tags[1] = "E_" + typeName;
                return StringUtils.join(tags, " ");
            }
            for (int i = 0; i < len; i++) {
                if (RuleDataLoad.checkWordType(words[i], typeName)) {
                    tags[i] = "S_" + typeName;
                }
            }
            return StringUtils.join(tags, " ");
        }
        if (len == 3) {
            if (RuleDataLoad.checkWordType(inputSent, typeName)) {
                tags[0] = "B_" + typeName;
                tags[1] = "I_" + typeName;
                tags[2] = "E_" + typeName;
                return StringUtils.join(tags, " ");
            }
            for (int i = 0; i < len - 1; i++) {
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
                if (RuleDataLoad.checkWordType(tmpWords, typeName)) {
                    tags[i] = "B_" + typeName;
                    tags[i + 1] = "E_" + typeName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
            for (int i = 0; i < len; i++) {
                String tmpWords = words[i];
                if (modifiedFlags[i] == 1)
                    continue;
                if (RuleDataLoad.checkWordType(tmpWords, typeName)) {
                    tags[i] = "S_" + typeName;
                }
            }
            return StringUtils.join(tags, " ");
        }
        if (len >= 4) {
            for (int i = 0; i < len - 3; i++) {
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 4), " ");
                if (RuleDataLoad.checkWordType(tmpWords, typeName)) {
                    tags[i] = "B_" + typeName;
                    tags[i + 1] = "I_" + typeName;
                    tags[i + 2] = "I_" + typeName;
                    tags[i + 3] = "E_" + typeName;
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
                if (RuleDataLoad.checkWordType(tmpWords, typeName)) {
                    tags[i] = "B_" + typeName;
                    tags[i + 1] = "I_" + typeName;
                    tags[i + 2] = "E_" + typeName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                    modifiedFlags[i + 2] = 1;
                }
            }
            for (int i = 0; i < len - 1; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
                if (RuleDataLoad.checkWordType(tmpWords, typeName)) {
                    tags[i] = "B_" + typeName;
                    tags[i + 1] = "E_" + typeName;
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
            for (int i = 0; i < len; i++) {
                if (modifiedFlags[i] == 1)
                    continue;
                String tmpWords = words[i];
                if (RuleDataLoad.checkWordType(tmpWords, typeName)) {
                    tags[i] = "S_" + typeName;
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
        int len = tags.length;
        String[] conditionTags1 = {"S_catemodifier", "O", "S_modifier"};
        String[] conditionTags2 = {"S_category", "B_category"};
        List<String> conditionsTags1List = Arrays.asList(conditionTags1);
        List<String> conditionsTags2List = Arrays.asList(conditionTags2);
        for (int i = 0; i < len - 2; i++) {
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
            if (RuleDataLoad.isStyle(tmpWords) && "S_category".equals(tags[i + 2])) {
                tags[i] = "B_style";
                tags[i + 1] = "E_style";
            }
        }
        for (int i = 0; i < len - 1; i++) {
//            String tmpWords = StringUtils.join(wordsList.subList(i, i+2), " ");
            if ("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1]) && RuleDataLoad.isStyle(words[i])) {
                tags[i] = "S_style";
                tags[i + 1] = "S_category";
            }
            if (conditionsTags1List.contains(tags[i]) && conditionsTags2List.contains(tags[i + 1]) &&
                    RuleDataLoad.isStyle(words[i])) {
                tags[i] = "S_style";
            }
        }
        for (int i = 0; i < len - 2; i++) {
            if ("S_population".equals(tags[i + 1]) && "S_category".equals(tags[i + 1]) && RuleDataLoad.isStyle(words[i]))
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
        int len = tags.length;
        String[] conditionTags1 = {"S_catemodifier", "O", "S_modifier"};
        String[] conditionTags2 = {"S_category", "B_category"};
        List<String> conditionsTags1List = Arrays.asList(conditionTags1);
        List<String> conditionsTags2List = Arrays.asList(conditionTags2);
        for (int i = 0; i < len - 2; i++) {
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 2), " ");
            if (RuleDataLoad.isElementWord(tmpWords) && "S_category".equals(tags[i + 2])) {
                tags[i] = "B_element";
                tags[i + 1] = "E_element";
            }
        }
        for (int i = 0; i < len - 1; i++) {
//            String tmpWords = StringUtils.join(wordsList.subList(i, i+2), " ");
            if ("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1]) && RuleDataLoad.isElementWord(words[i])) {
                tags[i] = "S_element";
                tags[i + 1] = "S_category";
            }
            if (conditionsTags1List.contains(tags[i]) && conditionsTags2List.contains(tags[i + 1]) &&
                    RuleDataLoad.isElementWord(words[i])) {
                tags[i] = "S_element";
            }
        }
        for (int i = 0; i < len - 2; i++) {
            if ("S_population".equals(tags[i + 1]) && "S_category".equals(tags[i + 2]) && RuleDataLoad.isElementWord(words[i]))
                tags[i] = "S_element";
        }
        return StringUtils.join(tags, " ");
    }

    /**
     * 型号词修正
     * @param inputSent
     * @param modifiedTags
     * @return
     */
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
                if (IsTypeWord.isTypeWords(wordsList) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]))) {
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
                if (IsTypeWord.isTypeWords(subList) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]) || "O".equals(tags[i + 2]))) {
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
                if (IsTypeWord.isTypeWords(subList) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]))) {
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
                if (IsTypeWord.isTypeWords(subList)) {
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
                if (IsTypeWord.isTypeWords(subList) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]) || "O".equals(tags[i + 2]))) {
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
                if (IsTypeWord.isTypeWords(subList) && ("O".equals(tags[i]) || "O".equals(tags[i + 1]))) {
                    tags[i] = "B_type";
                    tags[i + 1] = "E_type";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
        }
        return StringUtils.join(tags, " ");
    }

    /**
     * 品类词修正
     * @param inputSent
     * @param modifiedTags
     * @return
     */
    private String beCategoryWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        int len = words.length;
        int[] modifiedFlags = new int[len];
        String[] conditionTags = {"S_catemodifier", "O", "S_modifier"};
        List<String> conditionTagsList = Arrays.asList(conditionTags);
        for (int i = 0; i < len - 2; i++) {
            if (modifiedFlags[i] == 1)
                continue;
            String tmpWords = StringUtils.join(wordsList.subList(i, i + 3), " ");
            if (RuleDataLoad.isBeCategory(tmpWords)) {
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
            if (RuleDataLoad.isBeCategory(tmpWords)) //&& (!tags[i].contains("category")))
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
                if (RuleDataLoad.isCateModifier(words[i])) {
                    tags[i] = "S_catemodifier";
                    tags[i + 1] = "S_category";
                    modifiedFlags[i] = 1;
                    modifiedFlags[i + 1] = 1;
                }
            }
        }

        return StringUtils.join(tags, " ");
    }

    /**
     * 品类修饰词修正
     * @param inputSent
     * @param modifiedTags
     * @return
     */
    private String catemodifierWordsModify(String inputSent, String modifiedTags) {
        String[] words = inputSent.split(" ");
        List<String> wordsList = Arrays.asList(words);
        String[] tags = modifiedTags.split(" ");
        int len = words.length;
        String[] conditionTags = {"S_catemodifier", "O", "S_modifier"};
        List<String> conditionTagsList = Arrays.asList(conditionTags);
        for (int i = 0; i < len - 1; i++) {
            if (("B_category".equals(tags[i]) && "E_category".equals(tags[i + 1])) ||
                    (conditionTagsList.contains(tags[i]) && "S_category".equals(tags[i + 1]))) {
                if (RuleDataLoad.isCateModifier(words[i])) {
                    tags[i] = "S_catemodifier";
                    tags[i + 1] = "S_category";
                }
            }
        }
        return StringUtils.join(tags, " ");
    }

    //modifystage
    public String modifyStage(String inputSent, String modifiedTags) {
        //先人群，再品牌，再ip，再功能
        String res = populationWordModify(inputSent, modifiedTags);
        res = brandWordModify(inputSent, res);
        res = ipWordModify(inputSent,res);
        res = functionWordModify(inputSent,res);

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
