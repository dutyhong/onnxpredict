package biz.stanfordNlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DurationExtract {
    public static String parseSentence(Annotation document) {
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = new ArrayCoreMap();
        if (sentences.size() >= 1) {
            sentence = sentences.get(0);
        }
        // traversing the words in the current sentence
        // a CoreLabel is a CoreMap with additional token-specific methods
        List<String> words = new ArrayList<>();
//        List<String> posTags = new ArrayList<>();
        List<String> nerResults = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            // this is the text of the token
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            words.add(word);
            // this is the POS tag of the token
//            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//            posTags.add(pos);
            // this is the NER label of the token
            String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            nerResults.add(ne);
        }
//        System.out.println("实体识别结果为：：");
//        System.out.println(nerResults.toString());
        // 根据ner输出结果进行后处理， 输出年龄
        int startPos = nerResults.size();
        int endPos = 0;
        if (nerResults.contains("DURATION")) {
            for (int i = 0; i < nerResults.size(); i++) {
                if ("DURATION".equals(nerResults.get(i))) {
                    if (i < startPos) {
                        startPos = i;
                    }
                    if (i >= endPos) {
                        endPos = i;
                    }
                }
            }
            List<String> nerDurationResults = nerResults.subList(startPos, endPos + 1);
            List<String> durationWords = words.subList(startPos, endPos + 1);
            String result = String.join(" ", durationWords);
            long end = System.currentTimeMillis();
            System.out.printf("耗时为：：%d\n", end - start);
            return result;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        props.setProperty("ner.useSUTime", "0");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String[] texts = {"heels boots kids 10 year old", "children clothes for boys from 7 to 11 years", "children clothes for six month boys", "clothes  3 to 6 day child",
                "children's wedding dresses for the age of 11", "baby girls ’clothes from the age of nine to ten"};
        for (int i = 0; i < texts.length; i++) {
            Annotation document = new Annotation(texts[i]);
            pipeline.annotate(document);
            String result = parseSentence(document);
            System.out.println(result);
        }
        String filePath = "/Users/duty/Downloads/tizi_test_2021_03_03.csv";
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String filePath2 = "/Users/duty/Downloads/stanford_date_extract";
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath2));
        String line = "";
        int cnt = 0;

        while ((line = br.readLine()) != null) {
            if (cnt > 10000)
                break;
            line = line.trim();
            String[] columns = line.split(",");
            if (columns.length != 7)
                continue;
            String txt = columns[6];
            Annotation document = new Annotation(txt);
            pipeline.annotate(document);
            String result = parseSentence(document);
//            System.out.println(result);
            if (result != null) {
                bw.write(txt + ";" + result + "\n");
            }
            cnt = cnt + 1;
        }
        br.close();
        bw.close();

    }
}
