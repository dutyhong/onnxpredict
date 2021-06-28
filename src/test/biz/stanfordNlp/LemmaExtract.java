package biz.stanfordNlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LemmaExtract {
    /***
     * 归一化函数，词干提取，单复数归一
     * @param document
     * @return
     */
    public static String parseSentence(Annotation document) {
        //分句，一定要分句
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = new ArrayCoreMap();
        if (sentences.size() >= 1)
            sentence = sentences.get(0);
        if (null == sentence || sentence.size() == 0)
            return null;
        List<String> lemmaWords = new ArrayList<>();
        for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String lemmaWord = token.get(CoreAnnotations.LemmaAnnotation.class);
            lemmaWords.add(lemmaWord);
        }
        return String.join(" ", lemmaWords);

    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
//        props.setProperty("ner.useSUTime", "0");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document1 = new Annotation("heels boots kids 10 year old");
        pipeline.annotate(document1);
        String[] texts = {"playing games kids 10 year old", "children clothing for boys from 7 to 11 years", "children clothes for six month boys", "clothes  3 to 6 day child",
                "children's wedding dresses for the age of 11", "baby girls ’clothes from the age of nine to ten"};
        for (int i = 0; i < texts.length; i++) {
            long start = System.currentTimeMillis();
            Annotation document = new Annotation(texts[i].replace("'s", ""));
            pipeline.annotate(document);
            String result = parseSentence(document);
            long end = System.currentTimeMillis();
            System.out.println(result);
            System.out.printf("每个句子耗时为：：%d\n", end - start);
        }
        System.out.println("ddddd");
    }
}
