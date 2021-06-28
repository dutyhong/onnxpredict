package biz;

import java.util.List;
import java.util.Map;

public class SpanBertEntity2BizEntity {
    /***
     * 输入为spanbert输出的start 和end取最大index之后所对应的结果 [[5,0,0], [12,3,4]]
     * @param spanBertOutEntities
     */
    public static String[] oneTransfer(String originalSent, List<int[]> spanBertOutEntities, Map<Integer, String> index2Tag) {
        int size = spanBertOutEntities.size();
        String[] words = originalSent.split(" ");
        String[] tags = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            tags[i] = "O";
        }
        for (int i = 0; i < size; i++) {
            int startId = spanBertOutEntities.get(i)[0];
            int endId = spanBertOutEntities.get(i)[1];
            String cateName = index2Tag.get(spanBertOutEntities.get(i)[2]);
            if (endId - startId == 0) {
                tags[startId] = "S_" + cateName;
            } else if (endId - startId == 1) {
                tags[startId] = "B_" + cateName;
                tags[endId] = "E_" + cateName;
            } else {
                int diffLen = endId - startId;
                String startTag = "B_" + cateName;
                String endTag = "E_" + cateName;
//                String[] middleTags = new String[diffLen];
                for (int j = 0; j < diffLen - 1; j++) {
                    tags[j + startId + 1] = "I_" + cateName;
                }
                tags[startId] = "B_" + cateName;
                tags[endId] = "E_" + cateName;

            }
        }
        return tags;

    }
//            List<String> tmpWords = Arrays.asList(words).subList(startId, endId+1);
//            String subWords = StringUtils.join(tmpWords, " ");
////            bizOneOutput.put(subWords, cateName);
//            int[] tmpindex = new int[2];
//            tmpindex[0] = startId;
//            tmpindex[1] = endId;
//            indice.add(tmpindex);
//            bizOneOutputs.put(startId, bizOneOutput);
//            indice.add(tmpindex);
//        }
//        for(int i=0; i<indice.size(); i++)
//        {
//            int startId = indice.get(i)[0];
//            int endId = indice.get(i)[1];
////            String cateName = bizOneOutputs.get(i)
//            for(int j=startId; j<=endId; j++)
//            {
//                tags[j] = "S";
//            }
//        }
//
//        for(int i=0; i<tags.length; i++)
//        {
//            if("O".equals(tags[i]))
//            {
//                Map<String, String> tmpMap = new HashMap<>();
//                tmpMap.put(words[i], "O");
//                bizOneOutputs.put(i, tmpMap);
//            }
//        }
//        return bizOneOutputs;


//    public static List<Map<Integer,Map<String, String>>> listTransfer(List<String> originalSents, List<List<int[]>> spanBertOutEntities, Map<Integer, String> index2Tag)
//    {
//        assert originalSents.size()==spanBertOutEntities.size();
//        List<Map<Integer,Map<String, String>>> listBizOutputs = new ArrayList<>();
//        for(int i=0; i<originalSents.size(); i++)
//        {
//            String oneOriginalSent = originalSents.get(i);
//            List<int[]> oneSpanBertOutEntities = spanBertOutEntities.get(i);
//            Map<Integer,Map<String, String >> oneBizOutputs = oneTransfer(oneOriginalSent, oneSpanBertOutEntities, index2Tag);
//            listBizOutputs.add(oneBizOutputs);
//        }
//        return listBizOutputs;
//    }
}
