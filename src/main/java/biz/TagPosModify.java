package biz;

import biz.dto.TokenNerTag;
import edu.stanford.nlp.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagPosModify {
    /***
     * 对模型的输出进行预处理
     * 1. 单个 B I E 转换为S
     * 2. BS BI转换为BE
     * @param inputSent
     * @param modifiedTags
     * @return
     */
    public String tagPosModifyModelOutput(String inputSent, String modifiedTags) {
        List<TokenNerTag> modelOutput = new ArrayList<>();
        String[] oriWords = inputSent.split(" ");
        String[] oriTags = modifiedTags.split(" ");
        for (int i = 0; i < oriWords.length; i++) {
            TokenNerTag tokenNerTag = new TokenNerTag(oriWords[i], oriTags[i]);
            modelOutput.add(tokenNerTag);
        }
        int size = modelOutput.size();

        List<String> singlePos = new ArrayList<String>(Arrays.asList(new String[]{"B", "I", "E"}));
//        List<String>  = new ArrayList<String>(Arrays.asList(new String[]{"B", "I", "E"}));
        List<String> posList = new ArrayList<>();
        List<String> cateList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (modelOutput.get(i).getTag().equals("O")) {
                posList.add("O");
                cateList.add("O");
            } else {
                String[] posCate = modelOutput.get(i).getTag().split("_");
                posList.add(posCate[0]);
                cateList.add(posCate[1]);
            }
        }
        // 前后各加一个O
        posList.add(0, "O");
        posList.add("O");
        cateList.add(0, "O");
        cateList.add("O");
        for (int i = 0; i < size; i++) {
            //当前位置为 B I E 但是前后类别不相等的时候
            if (singlePos.contains(posList.get(i + 1)) && !cateList.get(i).equals(cateList.get(i + 1)) && !cateList.get(i + 1).equals(cateList.get(i + 2))) {
                String newPosCate = "S_" + cateList.get(i + 1);
                modelOutput.get(i).setTag(newPosCate);
            }
        }
        for (int i = 0; i < size - 1; i++) {
            //BS BI类别相同，前一个和后一个类别不同
            if ((cateList.get(i + 1).equals(cateList.get(i + 2)) && ((posList.get(i + 1).equals("B") && posList.get(i + 2).equals("S")) || (posList.get(i + 1).equals("B") && posList.get(i + 2).equals("I"))))
                    && ((!cateList.get(i).equals(cateList.get(i + 1)) || posList.get(i).equals("E")) && !cateList.get(i + 2).equals(cateList.get(i + 3)))) {
                String newPosCate = "E_" + cateList.get(i + 1);
                modelOutput.get(i + 1).setTag(newPosCate);
            }
        }
        for (int i = 0; i < size - 1; i++) {
            //SE修正为BE
            if ((cateList.get(i + 1).equals(cateList.get(i + 2)) && ((posList.get(i + 1).equals("S") && posList.get(i + 2).equals("E")))
                    && ((!cateList.get(i).equals(cateList.get(i + 1)) || posList.get(i).equals("E")) && !cateList.get(i + 2).equals(cateList.get(i + 3))))) {
                String newPosCate = "B_" + cateList.get(i + 1);
                modelOutput.get(i).setTag(newPosCate);
            }
        }
        for (int i = 0; i < size - 2; i++) {
            //BII类别相同，前一个和后一个类别不同 或者前一个为E
            if (posList.get(i + 1).equals("B") && posList.get(i + 2).equals("I") && posList.get(i + 3).equals("I") && cateList.get(i + 1).equals(cateList.get(i + 2)) && cateList.get(i + 2).equals(cateList.get(i + 3)) &&
                    (posList.get(i).equals("E") || !cateList.get(i).equals(cateList.get(i + 1))) && !cateList.get(i + 3).equals(cateList.get(i + 4))) {
                String newPosCate = "E_" + cateList.get(i + 3);
                modelOutput.get(i + 2).setTag(newPosCate);
            }
        }
        //重新转换回去
        String[] newModifiedTags = new String[oriTags.length];
        for (int i = 0; i < modelOutput.size(); i++) {
            newModifiedTags[i] = modelOutput.get(i).getTag();
        }
        return StringUtils.join(newModifiedTags, " ");
    }
}
