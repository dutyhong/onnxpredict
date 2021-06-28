package biz;

import biz.dto.TokenNerTag;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelOutputAnalyze {
    private static String pattern1 = "S";
    private static String pattern2 = "BE";
    private static String pattern3 = "BI+E";
    private static String pattern4 = "O+";
    private static Pattern p1 = Pattern.compile(pattern1);
    private static Pattern p2 = Pattern.compile(pattern2);
    private static Pattern p3 = Pattern.compile(pattern3);
    private static Pattern p4 = Pattern.compile(pattern4);

    public static void main(String[] args) {
        String aa = "SSOBEBIIOEBE";
        String bb = "S_category S_category O B_category E_category B_category I_category I_category S_ip O B_category E_category";
        String cc = "dress shirt for school bag bmw car perfume in car ipad laptop";
        List<String> nerTags = Arrays.asList(bb.split(" "));
        List<String> tokens = Arrays.asList(cc.split(" "));
        ModelOutputAnalyze modelOutputAnalyze = new ModelOutputAnalyze();
//        List<Map<String, String>> result = modelOutputAnalyze.tagRegMatch(aa, nerTags, tokens);
        List<TokenNerTag> tokenNerTags = new ArrayList<>();
        for (int i = 0; i < nerTags.size(); i++) {
            TokenNerTag tokenNerTag = new TokenNerTag(tokens.get(i), nerTags.get(i));
            tokenNerTags.add(tokenNerTag);
        }
        List<TokenNerTag> processTokenNerTags = modelOutputAnalyze.processModelOutput(tokenNerTags);
        List<Map<String, String>> result = modelOutputAnalyze.modelOutputAnalyze(processTokenNerTags);

        System.out.println("dddd");
    }

    /***
     * 根据模型输出结果：gucci school bag S_brand, B_category I_category 解析成gucci:brand school bag category
     * @param modelOutput:每个token对应的实体类别
     * @return
     */
    public List<Map<String, String>> modelOutputAnalyze(List<TokenNerTag> modelOutput) {

        List<TokenNerTag> processModelOutput = processModelOutput(modelOutput);
        List<Map<String, String>> result = new ArrayList<>();
//        Map<String, String> tmpResultMap = new HashMap<>();
        List<String> tokens = new ArrayList<>();
        List<String> nerTags = new ArrayList<>();
        StringBuilder tagPosition = new StringBuilder();
        for (int i = 0; i < processModelOutput.size(); i++) {
            TokenNerTag tokenNerTag = processModelOutput.get(i);
            String token = tokenNerTag.getToken();
            tokens.add(token);
            String nerTag = tokenNerTag.getTag();
            nerTags.add(nerTag);
            if ("O".equals(nerTag))
                tagPosition.append("O");
            else {
                tagPosition.append(nerTag.split("_")[0]);
            }
        }
        result = tagRegMatch(tagPosition.toString(), nerTags, tokens);
        return result;
    }

    /***
     * 对模型的输出进行预处理
     * 1. 单个 B I E 转换为S
     * 2. BS BI转换为BE
     * @param modelOutput
     * @return
     */
    public List<TokenNerTag> processModelOutput(List<TokenNerTag> modelOutput) {
        int size = modelOutput.size();
        List<String> singlePos = new ArrayList<String>(Arrays.asList(new String[]{"B", "I", "E"}));
//        List<String>  = new ArrayList<String>(Arrays.asList(new String[]{"B", "I", "E"}));
        List<String> posList = new ArrayList<>();
        List<String> cateList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (modelOutput.get(i).getTag().equals("O")) {
                posList.add("O");
                cateList.add("O");
            } else {
                String[] posCate = modelOutput.get(i).getTag().split("_");
                posList.add(posCate[0]);
                cateList.add(posCate[1]);
            }
        }
        // 前后各加一个O
        posList.add(0, "O");
        posList.add("O");
        cateList.add(0, "O");
        cateList.add("O");
        for (int i = 0; i < size; i++) {
            //当前位置为 B I E 但是前后类别不相等的时候
            if (singlePos.contains(posList.get(i + 1)) && !cateList.get(i).equals(cateList.get(i + 1)) && !cateList.get(i + 1).equals(cateList.get(i + 2))) {
                String newPosCate = "S_" + cateList.get(i + 1);
                modelOutput.get(i).setTag(newPosCate);
            }
        }
        for (int i = 0; i < size - 1; i++) {
            //BS BI类别相同，前一个和后一个类别不同
            if ((cateList.get(i + 1).equals(cateList.get(i + 2)) && ((posList.get(i + 1).equals("B") && posList.get(i + 2).equals("S")) || (posList.get(i + 1).equals("B") && posList.get(i + 2).equals("I"))))
                    && ((!cateList.get(i).equals(cateList.get(i + 1)) || posList.get(i).equals("E")) && !cateList.get(i + 2).equals(cateList.get(i + 3)))) {
                String newPosCate = "E_" + cateList.get(i + 1);
                modelOutput.get(i + 1).setTag(newPosCate);
            }
        }
        for (int i = 0; i < size - 1; i++) {
            //SE修正为BE
            if ((cateList.get(i + 1).equals(cateList.get(i + 2)) && ((posList.get(i + 1).equals("S") && posList.get(i + 2).equals("E")))
                    && ((!cateList.get(i).equals(cateList.get(i + 1)) || posList.get(i).equals("E")) && !cateList.get(i + 2).equals(cateList.get(i + 3))))) {
                String newPosCate = "B_" + cateList.get(i + 1);
                modelOutput.get(i).setTag(newPosCate);
            }
        }
        for (int i = 0; i < size - 2; i++) {
            //BII类别相同，前一个和后一个类别不同 或者前一个为E
            if (posList.get(i + 1).equals("B") && posList.get(i + 2).equals("I") && posList.get(i + 3).equals("I") && cateList.get(i + 1).equals(cateList.get(i + 2)) && cateList.get(i + 2).equals(cateList.get(i + 3)) &&
                    (posList.get(i).equals("E") || !cateList.get(i).equals(cateList.get(i + 1))) && !cateList.get(i + 3).equals(cateList.get(i + 4))) {
                String newPosCate = "E_" + cateList.get(i + 3);
                modelOutput.get(i + 2).setTag(newPosCate);
            }
        }
        return modelOutput;
    }

    public List<Map<String, String>> tagRegMatch(String tags, List<String> nerTags, List<String> tokens) {

        Matcher matcher1 = ModelOutputAnalyze.p1.matcher(tags);
        Matcher matcher2 = ModelOutputAnalyze.p2.matcher(tags);
        Matcher matcher3 = ModelOutputAnalyze.p3.matcher(tags);
        Matcher matcher4 = ModelOutputAnalyze.p4.matcher(tags);
        Map<Integer, Map<String, String>> result = new TreeMap<>();
        //提取出的实体的索引位置，其他的索引位置都是O
        List<Integer> tagIndice = new ArrayList<>();
//        Map<String, String> tt = new HashMap<>();
        while (matcher1.find()) {
            for (int i = 0; i <= matcher1.groupCount(); i++) {
                int start = matcher1.start();
                int end = matcher1.end();
                for (int j = start; j < end; j++) {
                    tagIndice.add(j);
                }
                List<String> subList = tokens.subList(start, end);
                String tmpStr = String.join("", subList);
                String currentNerTag = nerTags.get(start);
                String category = currentNerTag.split("_")[1];
//                String token = tokens.get(start);
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put(tmpStr, category);
                result.put(start, tmpMap);
                System.out.println(matcher1.start() + ";" + matcher1.end());
//                System.out.println(i+":"+matcher1.group());
            }
        }
        while (matcher2.find()) {
            for (int i = 0; i <= matcher2.groupCount(); i++) {
                int start = matcher2.start();
                int end = matcher2.end();
                for (int j = start; j < end; j++) {
                    tagIndice.add(j);
                }
                List<String> subList = tokens.subList(start, end);
                String tmpStr = String.join(" ", subList);
                String currentNerTag = nerTags.get(start);
                String category = currentNerTag.split("_")[1];
//                String token = tokens.get(start);
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put(tmpStr, category);
                result.put(start, tmpMap);
                System.out.println(matcher2.start() + ";" + matcher2.end());
                System.out.println(i + ":" + matcher2.group());
            }
        }
        while (matcher3.find()) {
            for (int i = 0; i <= matcher3.groupCount(); i++) {
                int start = matcher3.start();
                int end = matcher3.end();
                for (int j = start; j < end; j++) {
                    tagIndice.add(j);
                }
                List<String> subList = tokens.subList(start, end);
                String tmpStr = String.join(" ", subList);
                String currentNerTag = nerTags.get(start);
                String category = currentNerTag.split("_")[1];
//                String token = tokens.get(start);
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put(tmpStr, category);
                result.put(start, tmpMap);
                System.out.println(matcher3.start() + ";" + matcher3.end());
                System.out.println(i + ":" + matcher3.group());
            }
        }
//        while(matcher4.find())
//        {
//            for(int i=0; i<=matcher4.groupCount(); i++)
//            {
//                int start = matcher4.start();
//                int end = matcher4.end();
//                for(int j=start; j<end; j++)
//                {
//                    tagIndice.add(j);
//                }
//                List<String> subList = tokens.subList(start, end);
//                String tmpStr = String.join( " ", subList);
//                String currentNerTag = nerTags.get(start);
//                String category = currentNerTag;
////                String token = tokens.get(start);
//                Map<String, String> tmpMap = new HashMap<>();
//                tmpMap.put(tmpStr, category);
//                result.put(start, tmpMap);
//                System.out.println(matcher4.start()+";"+matcher4.end());
//                System.out.println(i+":"+matcher4.group());
//            }
//        }
        int[] allIndice = new int[nerTags.size()];
        List<Integer> oIndice = new ArrayList<>();
        for (int j = 0; j < allIndice.length; j++) {
            if (!tagIndice.contains(j)) {
                oIndice.add(j);
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put(tokens.get(j), "O");
                result.put(j, tmpMap);
            }
        }
        //对最终map按照索引进行排序 转换为list
        List<Map<String, String>> newResult = new ArrayList<>();
        result.forEach((key, value) -> {
            newResult.add(value);
        });
        return newResult;
    }
}
