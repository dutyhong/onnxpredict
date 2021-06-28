package biz;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagSent2TagMap {
    private static String pattern1 = "S";
    private static String pattern2 = "BE";
    private static String pattern3 = "BI+E";
    private static String pattern4 = "O+";
    private static Pattern p1 = Pattern.compile(pattern1);
    private static Pattern p2 = Pattern.compile(pattern2);
    private static Pattern p3 = Pattern.compile(pattern3);
    private static Pattern p4 = Pattern.compile(pattern4);

    public static List<Map<String, String>> tagSent2TagMap(String postionTags, List<String> nerTags, List<String> tokens) {

        Matcher matcher1 = p1.matcher(postionTags);
        Matcher matcher2 = p2.matcher(postionTags);
        Matcher matcher3 = p3.matcher(postionTags);
        Matcher matcher4 = p4.matcher(postionTags);
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
//                System.out.println(matcher1.start()+";"+matcher1.end());
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
//                System.out.println(matcher2.start()+";"+matcher2.end());
//                System.out.println(i+":"+matcher2.group());
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
//                System.out.println(matcher3.start()+";"+matcher3.end());
//                System.out.println(i+":"+matcher3.group());
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

    public static void main(String[] args) {
        //dresses 5 years old;O O O O;S_type B_age I_age E_age
        //dresses age 5;O O O;O B_age E_age
        String[] aa = "dresses age 5".split(" ");
        String[] bb = "O B_age E_age".split(" ");
        List<String> tokens = new ArrayList<>();
        List<String> nerTags = new ArrayList<>();
        StringBuilder tagPosition = new StringBuilder();
        for (int i = 0; i < aa.length; i++) {
            tokens.add(aa[i]);
            nerTags.add(bb[i]);
            if ("O".equals(bb[i])) {
                tagPosition.append("O");
            } else {
                tagPosition.append(bb[i].split("_")[0]);

            }
        }
        List<Map<String, String>> newResult = tagSent2TagMap(tagPosition.toString(), nerTags, tokens);
        System.out.println("ddd");

    }

}
