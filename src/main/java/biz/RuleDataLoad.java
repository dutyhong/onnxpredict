package biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RuleDataLoad {
    public Set<String> elementWords = new HashSet<>();
    public Set<String> styleWords = new HashSet<>();
    public Set<String> beCategoryWords = new HashSet<>();
    public Set<String> catemodifierWords = new HashSet<>();
    public Set<String> typeWords = new HashSet<>();
    public Map<String, String> beforePredictMatchDict = new HashMap<>();

    public RuleDataLoad() {
        BufferedReader br = null;
        String tmpStr = null;
        //直接替换规则的词读取
        try {
            Class<ModelPredictResultModify> ds = ModelPredictResultModify.class;
            InputStream path = RuleDataLoad.class.getResourceAsStream("/match_words_dict");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();
                String[] columns = tmpStr.split(",");
                if (columns.length != 2)
                    continue;
                beforePredictMatchDict.put(columns[0], columns[1]);
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }
        // 风格 款式 品类 品类修饰词读取
        try {
            InputStream path = RuleDataLoad.class.getResourceAsStream("/element_words");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();
                elementWords.add(tmpStr);
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }

        try {
            InputStream path = RuleDataLoad.class.getResourceAsStream("/style_words");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();
                styleWords.add(tmpStr);
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }

        try {
            InputStream path = RuleDataLoad.class.getResourceAsStream("/type_words");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();
                typeWords.add(tmpStr);
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }

        try {
            InputStream path = RuleDataLoad.class.getResourceAsStream("/catemodifier_words");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();
                catemodifierWords.add(tmpStr);
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }

        try {
            InputStream path = RuleDataLoad.class.getResourceAsStream("/be_category_tmp");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();

                String[] columns = tmpStr.split(";");
                if (columns.length != 2)
                    continue;
                beCategoryWords.add(columns[0]);
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RuleDataLoad ruleDataLoad = new RuleDataLoad();
        System.out.println("ddd");
    }
}
