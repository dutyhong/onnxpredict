package onnx;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import biz.ModelOutput2BizOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpanBert2BizOutput {

    public static String[] modelOutput2BizOutput(OrtSession session, OrtEnvironment env, int maxSeqLen, String sents, Map<Integer, String> index2Tag) throws Exception {
        //构造输入数据
        String modelName = "spanbert";
        List<String> batchSents = new ArrayList<>();
        batchSents.add(sents);
        List<List<long[][]>> batchOutput = InputDataGenerator.batchSentenceGenerate(batchSents, maxSeqLen, modelName);
        Map<String, OnnxTensor> singlePredictData = CreatePredictData.singlePredictData(batchOutput, env, session, 0, modelName);
        //开始预测
        long start = System.currentTimeMillis();
        OrtSession.Result result = session.run(singlePredictData);

        long end = System.currentTimeMillis();
        System.out.printf("一个预测耗时为：：%d\n", (end - start));
        //预测完关闭tensor
        for (Map.Entry<String, OnnxTensor> entry : singlePredictData.entrySet()) {
            entry.getValue().close();
        }
        singlePredictData.clear();

        //获取预测值进行处理
        float[][][] tmpStartLogits = (float[][][]) result.get(0).getValue();
        float[][][] tmpEndLogits = (float[][][]) result.get(1).getValue();
        //只有一句
        float[][] startLogits = tmpStartLogits[0];
        float[][] endLogits = tmpEndLogits[0];
        //根据模型输出开始和结束的值 处理输出实体
//        NerTagLoad.tagLoad("span_ner_tags");
        String[] results = ModelOutput2BizOutput.modelOutputLogits2Entity(sents, startLogits, endLogits, maxSeqLen, index2Tag);
//        int seqLen = maxSeqLen;
//        int tagCnt = index2Tag.size();
//        List<int[]> entities = new ArrayList<>();
//        //entities里面可能会有重叠 如[5,0,2] [5,1,2]
//        entities = SpanBertExtractEntity.entityExtractFromSpanBertOutputs(startLogits, endLogits, maxSeqLen);
//        for(int i=0; i<entities.size(); i++)
//        {
//            int[] entity = entities.get(i);
//            int startId = entity[0];
//            int endId = entity[1];
//            String tagName = index2Tag.get(entity[2]);
//            System.out.println(startId+","+endId+","+tagName);
//        }
////        Map<Integer, Map<String, String>> results = SpanBertEntity2BizEntity.oneTransfer(batchSents.get(0), entities,index2Tag);
//        String[] results = SpanBertEntity2BizEntity.oneTransfer(batchSents.get(0), entities,index2Tag);
//        System.out.println(results.toString());
        return results;
    }
}
