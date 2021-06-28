package biz;

import biz.dto.AgeInterval;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgeExtract {
    public static StanfordCoreNLP pipeline = null;
    public static Map<String, String> digitsMap = new HashMap<String, String>() {{
        put("one", "1");
        put("two", "2");
        put("three", "3");
        put("four", "4");
        put("five", "5");
        put("six", "6");
        put("seven", "7");
        put("eight", "8");
        put("nine", "9");
        put("ten", "10");
    }};
    //数字正则匹配
    private static Pattern p1 = Pattern.compile("\\d+");
    private static Pattern p2 = Pattern.compile("age\\s*[a-z]* \\d+");
    private static Pattern p3 = Pattern.compile("age\\s*[a-z]* \\d+\\s[a-z]* \\d+");

    /***
     * 预加载斯坦福的模型文件和配置
     */
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
//        props.setProperty("tokenize.options","untokenizable=noneKeep");
        props.setProperty("ner.useSUTime", "0");
        pipeline = new StanfordCoreNLP(props);
        Annotation document1 = new Annotation("heels boots kids 10 year old");
        pipeline.annotate(document1);
    }

    public static void main(String[] args) {
//        String[] texts = {"heels boots kids 10 year old", "children clothes for boys from 7 to 11 years", "children clothes for six month boys", "clothes 3 to 6 day child","clothes at the age of 17"};
        String[] texts = {"0~3 years", "0 3 years", "3 6 year t shirt", "boy clothes age 9"};
        AgeExtract ageExtract = new AgeExtract();
        for (int i = 0; i < texts.length; i++) {
            AgeInterval ageInterval = ageExtract.ageExtract(texts[i]);
            System.out.println(ageInterval.toString());
        }

        BufferedReader br = null;
        BufferedWriter bw = null;
        BufferedWriter bw2 = null;
        try {
//            br = new BufferedReader(new FileReader("/Users/duty/Documents/top_query_normalized_data_5"));
            br = new BufferedReader(new FileReader("/Users/duty/Downloads/tizi_test_2021_04_16.csv"));
            bw = new BufferedWriter(new FileWriter("age_extract_samples"));
            bw2 = new BufferedWriter(new FileWriter("all_age_samples"));
            String line = "";
            long start = System.currentTimeMillis();
            int pv = 0;
            int cnt = 0;
            while ((line = br.readLine()) != null) {
                String[] columns = line.trim().split(",");
                if (columns.length != 3)
                    continue;
                String normalizedQuery = columns[1];
                String[] words = normalizedQuery.split(" ");
                List<String> newWords = Arrays.asList(words);
                if (newWords.contains("years") || newWords.contains("year") || newWords.contains("month") || newWords.contains("months") || newWords.contains("day") || newWords.contains("days")
                        || newWords.contains("age")) {
                    cnt++;
                    bw2.write(line.trim() + "\n");
                }
                System.out.println(normalizedQuery);
                AgeInterval ageInterval = ageExtract.ageExtract(normalizedQuery);
                if (ageInterval != null) {
                    bw.write(normalizedQuery + ";" + ageInterval.toString() + "\n");
                    pv = pv + Integer.parseInt(columns[2]);
                }
            }
            System.out.println(pv);
            System.out.println(cnt);
            long end = System.currentTimeMillis();
            System.out.println("耗时为：：" + (end - start));
            br.close();
            bw.close();
            bw2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 通过stanfor nlp实体识别解析query中包含的年龄
     * @param query
     */
    private AgeInterval ageStanfordExtract(String query) {
        if (null == query)
            return null;
        String[] words = query.split(" ");
        int length = words.length;
        if (length <= 1)
            return null;
        List<String> wordsList = Arrays.asList(words);
        if (!wordsList.contains("year") && !wordsList.contains("years") && !wordsList.contains("months") && !wordsList.contains("month") && !wordsList.contains("day") && !wordsList.contains("days"))
            return null;
        Annotation document = new Annotation(query);
        pipeline.annotate(document);
        //分句，一定要分句
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = new ArrayCoreMap();
        if (sentences.size() >= 1)
            sentence = sentences.get(0);
        if (null == sentence || sentence.size() == 0)
            return null;
        List<String> nerResults = new ArrayList<>();
        List<String> newWords = new ArrayList<>();
        for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            String tmpWord = token.get(CoreAnnotations.TextAnnotation.class);
            nerResults.add(ner.toString());
            newWords.add(tmpWord);
        }
        //解析实体结果变成age:year\month\day min:1 max 5
        int durationIndexMin = newWords.size();
        int durationIndexMax = 0;
        for (int i = 0; i < nerResults.size(); i++) {
            if ("DURATION".equals(nerResults.get(i))) {
                if (i <= durationIndexMin)
                    durationIndexMin = i;
                if (i > durationIndexMax)
                    durationIndexMax = i;
            }
        }
        if (durationIndexMin == newWords.size() && durationIndexMax == 0)
            return null;
        //如果第一个是数字，将第一个数字和到后面的duration一起
        Matcher matcher11 = p1.matcher(newWords.get(0));
        String firstDigit = "";
        if (matcher11.find()) {
            firstDigit = matcher11.group();
            durationIndexMin = 0;
        }
        List<String> replaceWords = new ArrayList<>();
        for (int i = 0; i < newWords.size(); i++) {
            if (digitsMap.containsKey(newWords.get(i)))
                replaceWords.add(digitsMap.get(newWords.get(i)));
            else
                replaceWords.add(newWords.get(i));
        }
        if (durationIndexMax >= replaceWords.size())
            return null;
        List<String> subWords = replaceWords.subList(durationIndexMin, durationIndexMax + 1);
        String durationQuery = String.join(" ", subWords);
//        if(null==subWords||subWords.size()==0)
//            durationQuery = AgeWordsRegExtract.ageWordsRegExtract(query);
        String timeInterval = "";
        int intervalMax = 0;
        int intervalMin = 0;
        if (durationQuery.contains("year")) {
            timeInterval = "year";
        } else if (durationQuery.contains("month")) {
            timeInterval = "month";
        } else if (durationQuery.contains("day")) {
            timeInterval = "day";
        }
        //匹配数字
        Matcher matcher = p1.matcher(durationQuery);
        List<Integer> digits = new ArrayList<>();
        int digitCnt = 0;
        while (matcher.find()) {
            int digit = Integer.parseInt(matcher.group());
            digits.add(digit);
            digitCnt++;
            if (digitCnt >= 2)
                break;
        }
        if (digits.size() == 0)
            return null;
        if (digits.size() == 1) {
            intervalMin = digits.get(0);
            intervalMax = digits.get(0);
        }
        if (digits.size() == 2) {
            intervalMin = Math.min(digits.get(0), digits.get(1));
            intervalMax = Math.max(digits.get(0), digits.get(1));
        }
        if (digits.size() >= 3) {
            return null;
        }
        AgeInterval ageInterval = new AgeInterval(intervalMin, intervalMax, timeInterval);
        return ageInterval;
    }

    /**
     * 通过正则匹配提取年龄  the age of  , at the age of, at the age,
     *
     * @param query
     * @return
     */
    private AgeInterval ageRegExtract(String query) {
        if (null == query)
            return null;
        String[] words = query.split(" ");
        List<String> wordList = Arrays.asList(words);
        if (!wordList.contains("age"))
            return null;
        String tmpQuery = AgeWordsRegExtract.ageWordsRegExtract(query);
//        boolean isMatch = Pattern.matches(p2, query);
        String tmpAgeStr = "";

        Matcher matcher3 = p3.matcher(query);
        List<String> ageStrs = new ArrayList<>();
        while (matcher3.find()) {
            ageStrs.add(matcher3.group());
        }
        if (ageStrs.size() == 0) {
            Matcher matcher2 = p2.matcher(query);
            while (matcher2.find()) {
                ageStrs.add(matcher2.group());
            }
        }
        if (ageStrs.size() != 1)
            return null;
        if (null != tmpQuery) {
            tmpAgeStr = tmpQuery;
        } else {
            tmpAgeStr = ageStrs.get(0);
        }

        //匹配数字
        Matcher matcher1 = p1.matcher(tmpAgeStr);
        List<Integer> digits = new ArrayList<>();
        int digitCnt = 0;
        int intervalMax = 0;
        int intervalMin = 0;
        while (matcher1.find()) {
            int digit = Integer.parseInt(matcher1.group());
            digits.add(digit);
            digitCnt++;
            if (digitCnt >= 2)
                break;
        }
        if (digits.size() == 0)
            return null;
        if (digits.size() == 1) {
            intervalMin = digits.get(0);
            intervalMax = digits.get(0);
        }
        if (digits.size() == 2) {
            intervalMin = Math.min(digits.get(0), digits.get(1));
            intervalMax = Math.max(digits.get(0), digits.get(1));
        }
        if (digits.size() >= 3) {
            return null;
        }
        return new AgeInterval(intervalMin, intervalMax, "year");

    }

    public AgeInterval ageExtract(String query) {
        AgeInterval ageInterval = ageRegExtract(query);
        if (null != ageInterval)
            return ageInterval;
        ageInterval = ageStanfordExtract(query);
        return ageInterval;
    }
}
