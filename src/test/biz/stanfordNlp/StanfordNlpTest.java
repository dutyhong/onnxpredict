package biz.stanfordNlp;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//import java.awt.desktop.SystemEventListener;

/**
 * Created by duty on 2021/4/15.
 */
public class StanfordNlpTest {

    public static void main(String[] args) {

        StanfordNlpTest example = new StanfordNlpTest();

        example.runAllAnnotators();

    }

    public void runAllAnnotators() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        props.setProperty("ner.useSUTime", "0");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
//        String text = "gaming mouse keyboard for boys 7 to 10 years old"; // Add your text here!
        String[] texts = {"heels boots kids 10 year old", "children clothes for boys from 7 to 11 years", "children clothes for six month boys", "clothes  3 to 6 day child", "clothes at the age of 17"};
        String text = "wireless bluetooth speaker bluetooth speaker"; // Add your text here!
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        // create an empty Annotation just with the given text
        long start = System.currentTimeMillis();
        long end = 0;
        for (int i = 0; i < texts.length; i++) {
            start = System.currentTimeMillis();
            document = new Annotation(texts[i]);
            end = System.currentTimeMillis();
            System.out.printf("新建documents需要时间为：：%d\n", end - start);
            // run all Annotators on this text
            pipeline.annotate(document);
            end = System.currentTimeMillis();
            System.out.printf("新建pipeline需要时间为：：%d\n", end - start);
            parserOutput(document);
        }
        end = System.currentTimeMillis();
        System.out.printf("需要时间为：：%d\n", end - start);

    }

    public void parserOutput(Annotation document) {
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            List<String> words = new ArrayList<>();
            List<String> posTags = new ArrayList<>();
            List<String> nerResults = new ArrayList<>();
            long start = System.currentTimeMillis();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                words.add(word);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                posTags.add(pos);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                nerResults.add(ne);
            }
            long end = System.currentTimeMillis();
            System.out.printf("耗时为：：%d", end - start);
//            System.out.println("分词结果为：：");
//            System.out.println(words.toString());
//            System.out.println("词性标注结果为：：");
//            System.out.println(posTags.toString());
            System.out.println("实体识别结果为：：");
            System.out.println(nerResults.toString());
//            // this is the parse tree of the current sentence
//            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//            System.out.println("语法树：");
////            System.out.println(tree.toString());
//            Set<Constituent> treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());
//            for (Constituent constituent : treeConstituents)
//            {
//                if (constituent.label() != null && (constituent.label().toString().equals("VP") || constituent.label().toString().equals("NP")))
//                {
//                    System.err.println("found constituent: " + constituent.toString());
//                    System.err.println(tree.getLeaves().subList(constituent.start(), constituent.end() + 1));
//                }
//            }
//            // this is the Stanford dependency graph of the current sentence
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//            System.out.println("依存句法：");
//            System.out.println(dependencies.toString());
        }

        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
//        Map<Integer, CorefChain> graph =
//                document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    }
}
