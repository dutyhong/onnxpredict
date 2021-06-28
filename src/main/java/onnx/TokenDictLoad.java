package onnx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TokenDictLoad {
    private Map<String, Integer> tokenDict = new HashMap<>();

    public TokenDictLoad(String modelName) {
        BufferedReader br = null;
        String tmpStr = null;
        try {
            InputStream path = TokenDictLoad.class.getResourceAsStream("/" + modelName + "_new_words_ids");
            br = new BufferedReader(new InputStreamReader(path));
            while ((tmpStr = br.readLine()) != null) {
                tmpStr = tmpStr.trim();
                String[] columns = tmpStr.split(",");
                if (columns.length != 2)
                    continue;

                this.tokenDict.put(columns[0], Integer.parseInt(columns[1]));
            }
            br.close();
        } catch (IOException E) {
            E.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TokenDictLoad tokenDictLoad = new TokenDictLoad("spanbert");
        String sent = "bluetooth purple down jacket BMW DDF";
        sent = sent.toLowerCase();
        List<String> sentence = new ArrayList<>();

        String[] words = sent.split(" ");
        sentence.addAll(Arrays.asList(words));
        sentence.add(0, "[CLS]");
        sentence.add("[SEP]");
        List<Integer> ids = tokenDictLoad.encode(sentence);
        System.out.println("ddd");
    }

    public List<Integer> encode(List<String> tokens) {
        if (null == tokens || tokens.size() == 0) {
            return null;
        }
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (!this.tokenDict.containsKey(tokens.get(i))) {
                ids.add(this.tokenDict.get("[UNK]"));
            } else {
                ids.add(this.tokenDict.get(tokens.get(i)));
            }
        }
        return ids;
    }
}
