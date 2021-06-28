package biz.stanfordNlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NounPhraseExtract {
    public static String parseSentence(Annotation document) {
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = new ArrayCoreMap();
        if (sentences.size() >= 1) {
            sentence = sentences.get(0);
        }
        // traversing the words in the current sentence
        // a CoreLabel is a CoreMap with additional token-specific methods
        List<String> words = new ArrayList<>();
        List<String> posTags = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            // this is the text of the token
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            words.add(word);
            // this is the POS tag of the token
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            posTags.add(pos);
            // this is the NER label of the token
//            String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//            nerResults.add(ne);
        }
        System.out.println("词性标注结果为：：");
        System.out.println(posTags.toString());
        return null;
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        props.setProperty("ner.useSUTime", "0");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String[] texts = {"heels boots kids 10 year old", "children clothes for boys from 7 to 11 years", "children clothes for six month boys", "clothes  3 to 6 day child",
                "children's wedding dresses for the age of 11"};
        for (int i = 0; i < texts.length; i++) {
            Annotation document = new Annotation(texts[i]);
            pipeline.annotate(document);
            String result = parseSentence(document);
            System.out.println(result);
        }
    }
}
