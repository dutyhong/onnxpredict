package biz;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgeEntityExtract {
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
//    private static Pattern p4 = Pattern.compile("\\d+ \\s*[a-z]* \\d+ year");
//    private static Pattern p5 = Pattern.compile("\\d+ \\s*[a-z]* \\d+ year");

    public static void main(String[] args) {
//        String[] texts = {"heels boots kids 10 year old", "children clothes for boys from 7 to 11 years", "children clothes for six month boys", "clothes 3 to 6 day child","clothes at the age of 17"};
        String[] texts = {"0 3 year", "dresses age 5", "3 6 year t shirt", "0 3 year old fendi", "girl dress 6 to 14 year old baby", "6~14 years girl dress", "girl dress 11 year old", "boy clothes age 6 9"};
        AgeEntityExtract ageExtract = new AgeEntityExtract();
        for (int i = 0; i < texts.length; i++) {
            int len = texts[i].split(" ").length;
            String[] tags = new String[len];
            for (int j = 0; j < len; j++) {
                tags[j] = "O";
            }
            String tmp = StringUtils.join(tags, " ");
            String newTags = ageExtract.ageEntityTag(texts[i], tmp);
            System.out.println(newTags.toString());
        }
    }

    /***
     * 通过stanfor nlp实体识别解析query中包含的年龄
     * @param query
     */
    public String ageStanfordExtract(String query) {
        if (null == query)
            return null;
        String[] words = query.split(" ");
        int length = words.length;
        if (length <= 3)
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
        List<String> nerWords = new ArrayList<>();
        for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            nerResults.add(ner.toString());
            nerWords.add(token.get(CoreAnnotations.TextAnnotation.class));
        }
        if (!StringUtils.join(nerWords, " ").equals(query))
            return null;
        //解析实体结果变成age:year\month\day min:1 max 5
        int durationIndexMin = length;
        int durationIndexMax = 0;
        for (int i = 0; i < nerResults.size(); i++) {
            if ("DURATION".equals(nerResults.get(i))) {
                if (i <= durationIndexMin)
                    durationIndexMin = i;
                if (i > durationIndexMax)
                    durationIndexMax = i;
            }
        }
        if (durationIndexMin == length && durationIndexMax == 0)
            return null;
        //如果第一个是数字，将第一个数字和到后面的duration一起
        Matcher matcher11 = p1.matcher(words[0]);
        String firstDigit = "";
        if (matcher11.find()) {
            firstDigit = matcher11.group();
            durationIndexMin = 0;
        }
        List<String> replaceWords = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (digitsMap.containsKey(words[i]))
                replaceWords.add(digitsMap.get(words[i]));
            else
                replaceWords.add(words[i]);
        }
        if (durationIndexMax >= replaceWords.size())
            return null;
        List<String> subWords = replaceWords.subList(durationIndexMin, durationIndexMax + 1);
        String durationQuery = String.join(" ", subWords);
        if (replaceWords.size() <= (durationIndexMax + 1))
            return durationQuery;
        if ("old".equals(replaceWords.get(durationIndexMax + 1))) {
            durationQuery = durationQuery + " old";
        }
        return durationQuery;
    }

    /**
     * 通过正则匹配提取年龄  the age of  , at the age of, at the age,
     *
     * @param query
     * @return
     */
    public String ageRegExtract(String query) {
        if (null == query)
            return null;
        if (!query.contains("age"))
            return null;
//        boolean isMatch = Pattern.matches(p2, query);
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
        String tmpAgeStr = ageStrs.get(0);
        return tmpAgeStr;

    }

    public String ageEntityTag(String inputSent, String tags) {

        String matchAgeStr = ageRegExtract(inputSent);
        String stanfordAgeStr = ageStanfordExtract(inputSent);
        String regExtractAgeStr = AgeWordsRegExtract.ageWordsRegExtract(inputSent);
        String ageStr = "";
        if (null != regExtractAgeStr) {
            ageStr = regExtractAgeStr;
        } else {
            ageStr = stanfordAgeStr;
        }
        if (null == ageStr) {
            ageStr = matchAgeStr;
        }
        String[] modifiedTags = tags.split(" ");
        if (null != ageStr) {
            String[] ageWords = ageStr.split(" ");
            int len = ageWords.length;
            String[] oriWords = inputSent.split(" ");
            String[] subWords = new String[len];

            for (int i = 0; i < oriWords.length - len + 1; i++) {
                for (int j = 0; j < len; j++) {
                    subWords[j] = oriWords[i + j];
                }
                if (listEqual(ageWords, subWords)) {
                    if (len == 1)
                        modifiedTags[i] = "S_age";
                    else if (len == 2) {
                        modifiedTags[i] = "B_age";
                        modifiedTags[i + 1] = "E_age";
                    } else {
                        modifiedTags[i] = "B_age";
                        modifiedTags[i + len - 1] = "E_age";
                        String[] middleTags = new String[len - 2];
                        for (int n = 0; n < len - 2; n++) {
                            modifiedTags[i + 1 + n] = "I_age";
                        }
                    }
                    break;
                }
            }
        }
        return StringUtils.join(modifiedTags, " ");

    }

    public boolean listEqual(String[] ageWords, String[] subWords) {
        int len = subWords.length;
        for (int i = 0; i < len; i++) {
            if (!subWords[i].equals(ageWords[i]))
                return false;
        }
        return true;
    }
}
