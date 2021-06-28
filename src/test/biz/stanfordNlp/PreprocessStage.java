package biz.stanfordNlp;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreprocessStage {
    public static Map<String, String> replaceDic = new HashMap<String, String>() {
        {
            put("adida", "adidas");
            put("loui", "louis");
            put("herme", "hermes");
            put("van", "vans");
        }
    };

    /***
     * 预处理函数
     * @param input
     * @param pipeline
     * @return
     */
    public static String preprocessOut(String input, StanfordCoreNLP pipeline) {
        long start = System.currentTimeMillis();
        //预处理
        input = input.replace("'s", "").replaceAll("[’.;?']", "");

        Annotation document = new Annotation(input);
        pipeline.annotate(document);
        String result = LemmaExtract.parseSentence(document);
        String[] words = result.split(" ");
        String[] newWords = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (replaceDic.containsKey(words[i])) {
                word = replaceDic.get(words[i]);
            }
            newWords[i] = word;
        }
        String newResult = String.join(" ", newWords);
        long end = System.currentTimeMillis();
        return newResult;
    }

    /***
     * 线下预处理样本
     * @param pipeline
     */
    public static void normalizedTaggingData(StanfordCoreNLP pipeline) {
        BufferedReader br = null;
        BufferedWriter bw = null;
        Set<String> querySet = new HashSet<>();
        Map<String, String> queryMap = new HashMap<>();
        int cnt = 0;
        try {
            br = new BufferedReader(new FileReader(("/Users/duty/Downloads/top_query_to_tag.csv")));
            String line = br.readLine();
            long start = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                line = line.replace("\"", "");
//                line = line.replace()
                String[] columns = line.trim().split(",");
                if (columns.length != 2)
                    continue;
                String queryTrans = columns[0];
                String result = preprocessOut(queryTrans, pipeline);
                if (result == null)
                    continue;
//                result = result.replace("herme", "hermes");
//                if(!result.contains("adidas"))
//                    result = result.replace("adida", "adidas");
//                result = result.replace("loui", "louis");
                queryMap.put(queryTrans, result);
//                bw.write(result + "\n");
                cnt = cnt + 1;
                querySet.add(result);
            }
            long end = System.currentTimeMillis();
            System.out.printf("预处理耗时：：%d\n", end - start);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.printf("去重前有%d个query！！", cnt);
        System.out.printf("去重后有%d个query！！", querySet.size());
        try {
            bw = new BufferedWriter(new FileWriter("/Users/duty/Documents/top_query_normalized_data"));
//            for(String normalizedQuery:querySet)
//            {
//                bw.write(normalizedQuery+"\n");
//            }
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                bw.write(entry.getKey() + ";" + entry.getValue() + "\n");
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        StanfordCoreNLP pipeline = InitializedPipeline.pipeline;
        String text = "playing games kids 10 year old";
        String[] texts = {"playing games kids 10 year old", "children clothing for boys from 7 to 11 years", "children clothes for six month boys", "clothes  3 to 6 day child",
                "children's wedding dresses for the age of 11", "baby girls ’clothes from the age of nine to ten"};
        for (int i = 0; i < texts.length; i++) {
            String tmpText = texts[i];

            String processedResult = preprocessOut(tmpText, pipeline);
            System.out.println(processedResult);
        }
        normalizedTaggingData(pipeline);
        System.out.println("dddd");
    }
}
