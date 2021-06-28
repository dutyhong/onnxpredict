package biz.onnx;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import biz.SpanBertEntity2BizEntity;
import onnx.CreatePredictData;
import onnx.InputDataGenerator;
import onnx.NerTagLoad;
import util.SpanBertExtractEntity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpanBertOnnxPredict {

    public static void main(String[] args) throws Exception {
        String filePath = "/Users/duty/PycharmProjects/tz_nlp/nlp_ner_app/model/bert_span_model.onnx";

        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();

        opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);
        Map<String, String> configs = opts.getConfigEntries();
        opts.setInterOpNumThreads(100);
        opts.setIntraOpNumThreads(1);

        System.out.println("Loading model from " + filePath);
        OrtSession session = env.createSession(filePath, opts);

        System.out.println("Inputs:");
        for (NodeInfo i : session.getInputInfo().values()) {
            System.out.println(i.toString());
        }

        System.out.println("Outputs:");
        for (NodeInfo i : session.getOutputInfo().values()) {
            System.out.println(i.toString());
        }

        // 构造测试数据
        String[] aa = {"leather jacket woman", "nike shoe run for man", "phone case for samsung a50"};
        List<String> bb = Arrays.asList(aa);
        InputStream path = NerTagLoad.class.getResourceAsStream("/model_parameter");
        BufferedReader br = new BufferedReader(new InputStreamReader(path));
        String line = null;
        int maxSeqLen = 0;
        while ((line = br.readLine()) != null) {
            String[] columns = line.trim().split(":");
            maxSeqLen = Integer.parseInt(columns[1]);
            break;
        }
        //构造输入数据
        String modelName = "spanbert";
        List<List<long[][]>> batchOutput = InputDataGenerator.batchSentenceGenerate(bb, maxSeqLen, modelName);
        Map<String, OnnxTensor> singlePredictData = CreatePredictData.singlePredictData(batchOutput, env, session, 2, modelName);
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
        NerTagLoad.tagLoad("span_ner_tags");

        int seqLen = maxSeqLen;
        int tagCnt = NerTagLoad.index2Tag.size();
        List<int[]> entities = new ArrayList<>();
        entities = SpanBertExtractEntity.entityExtractFromSpanBertOutputs(startLogits, endLogits, maxSeqLen);
        for (int i = 0; i < entities.size(); i++) {
            int[] entity = entities.get(i);
            int startId = entity[0];
            int endId = entity[1];
            String tagName = NerTagLoad.index2Tag.get(entity[2]);
            System.out.println(startId + "," + endId + "," + tagName);
        }
        String[] results = SpanBertEntity2BizEntity.oneTransfer(aa[2], entities, NerTagLoad.index2Tag);
        System.out.println(results.toString());
//        float[][] outprobs = (float[][]) result.get(0).getValue();  //一个样本预测
//        System.out.println(Arrays.toString(outprobs[0])); // 类别logits
//
//
//        SparseData sparseData = SparseData.load("/Users/duty/Downloads/mnist.t");
//        float[][][][] testData = new float[1][1][28][28];
//        int cnt = 0;
//        for(int i=0; i<sparseData.labels.length; i++)
//        {
//            cnt++;
//            if (cnt>10)
//                break;
//            writeData(testData, sparseData.indices.get(i), sparseData.values.get(i));
//
//            OnnxTensor testTensor = OnnxTensor.createTensor(env, testData);
//            OrtSession.Result result = session.run(Collections.singletonMap(inputName, testTensor));
//            float[][] outprobs = (float[][]) result.get(0).getValue();  //一个样本预测
//            System.out.println(Arrays.toString(outprobs[0])); // 类别logits
//        }

    }
}
