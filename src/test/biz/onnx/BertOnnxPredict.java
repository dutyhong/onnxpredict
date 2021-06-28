package biz.onnx;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import onnx.CreatePredictData;
import onnx.InputDataGenerator;
import onnx.NerTagLoad;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BertOnnxPredict {

    private static int getMaxIndex(float[] arr) {
        float maxValue = Float.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > maxValue) {
                maxValue = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static void main(String[] args) throws Exception {
        String filePath = "/Users/duty/PycharmProjects/tz_nlp/nlp_ner_app/model/joint_bert_model_v4.onnx";

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
        String[] aa = {"search adida woman", "poluva swim clothes for woman"};
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
        List<List<long[][]>> batchOutput = InputDataGenerator.batchSentenceGenerate(bb, maxSeqLen, "bert");
        Map<String, OnnxTensor> singlePredictData = CreatePredictData.singlePredictData(batchOutput, env, session, 1, "bert");
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
        float[][][] tmpLogits = (float[][][]) result.get(1).getValue();
//        float[][][] tmpEndLogits = (float[][][]) result.get(2).getValue();
        //只有一句
        float[][] logits = tmpLogits[0];
//        float[][] endLogits = tmpEndLogits[0];
        //根据模型输出开始和结束的值 处理输出实体

        int seqLen = logits.length;
        int tagCnt = logits[0].length;
        NerTagLoad.tagLoad("ner_tags");
        String[] tags = new String[seqLen];
        for (int i = 0; i < seqLen; i++) {
            float[] tmpArr = logits[i];
            int tmpMaxIndex = getMaxIndex(tmpArr);
            String tag = NerTagLoad.index2Tag.get(tmpMaxIndex);
            tags[i] = tag;
        }
        System.out.println(Arrays.toString(tags));
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
