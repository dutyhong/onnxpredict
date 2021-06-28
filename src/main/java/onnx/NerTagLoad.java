package onnx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class NerTagLoad {
    public static Map<String, Integer> tag2Index = new HashMap<>();
    public static Map<Integer, String> index2Tag = new HashMap<>();

    //    private String filePath = this.getClass().getClassLoader().getResource("文件名").getPath();
    public static void tagLoad(String tag_file_name) {
//        tagFilepath = this.filePath;
        try {
            InputStream path = NerTagLoad.class.getResourceAsStream("/" + tag_file_name);
            BufferedReader br = new BufferedReader(new InputStreamReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length != 2) {
                    System.out.println("tag 文件格式不对！！！！");
                    continue;
                }
                String tag = columns[0];
                int index = Integer.parseInt(columns[1]);
                tag2Index.put(tag, index);
                index2Tag.put(index, tag);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        NerTagLoad.tagLoad("span_ner_tags");
        Map<String, Integer> tag2Index = NerTagLoad.tag2Index;
        Map<Integer, String> index2Tag = NerTagLoad.index2Tag;
        System.out.println("ddd");
    }
}
