package stanfordNlp;//package biz.stanfordNlp;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class InitializedPipeline {
    public static StanfordCoreNLP pipeline = null;

    /***
     * 预加载斯坦福的模型文件和配置
     */
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        props.setProperty("tokenize.options", "untokenizable=noneKeep");
//        props.setProperty("ner.useSUTime", "0");
        pipeline = new StanfordCoreNLP(props);
        Annotation document1 = new Annotation("heels boots kids 10 year old");
        pipeline.annotate(document1);
    }
}
