package biz;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import biz.dto.TokenNerTag;
import edu.stanford.nlp.util.StringUtils;
import onnx.CreatePredictData;
import onnx.InputDataGenerator;
import onnx.NerTagLoad;
import onnx.SpanBert2BizOutput;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpanBertNerPipeline {
    public static String nerPipeline(OrtSession session, OrtEnvironment env, int maxSeqLen, String sents, Map<Integer, String> index2Tag, AgeEntityExtract ageEntityExtract,
                                     ModelPredictResultModify modelPredictResultModify) throws Exception {
        //先将模型的输出转换为phone case for samsung a50;B_category E_category O S_brand S_type 格式
        String[] oneSentResult = SpanBert2BizOutput.modelOutput2BizOutput(session, env, maxSeqLen, sents, index2Tag);
        List<String> tags = new ArrayList<String>(Arrays.asList(oneSentResult));
        String[] inputWords = sents.split(" ");
        int len = inputWords.length;

        String posTagsStr = StringUtils.join(tags, " ");
        //年龄
        posTagsStr = ageEntityExtract.ageEntityTag(sents, posTagsStr);
        //修正项开始
        posTagsStr = modelPredictResultModify.modifyStage(sents, posTagsStr);
        return posTagsStr;
    }

    public static void main(String[] args) throws Exception {
        String filePath = "/Users/duty/PycharmProjects/tz_nlp/nlp_ner_app/model/span_bert.online.onnx";

        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();

        opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);
        Map<String, String> configs = opts.getConfigEntries();
        opts.setInterOpNumThreads(100);
        opts.setIntraOpNumThreads(1);
        OrtSession session = env.createSession(filePath, opts);


//        NerTagLoad.tagLoad("span_ner_tags");
        Map<Integer, String> index2Tag = NerTagLoad.index2Tag;
        InputStream path = NerTagLoad.class.getResourceAsStream("/model_parameter");
        BufferedReader br = new BufferedReader(new InputStreamReader(path));
        String line = null;
        int maxSeqLen = 0;
        while ((line = br.readLine()) != null) {
            String[] columns = line.trim().split(":");
            maxSeqLen = Integer.parseInt(columns[1]);
            break;
        }

        ModelPredictResultModify modelPredictResultModify = new ModelPredictResultModify();
        List<List<String>> results = new ArrayList<>();
//        path = SpanBertNerPipeline.class.getResourceAsStream("/src/test/new_test_data");
        String testPath = "/Users/duty/work/dl4j/src/test/new_test_data";
        br = new BufferedReader(new FileReader(testPath));
        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/duty/PycharmProjects/tz_nlp/data/tmp_span_bert_evaluate_result4j"));
        ModelOutputAnalyze modelOutputAnalyze = new ModelOutputAnalyze();
        AgeEntityExtract ageEntityExtract = new AgeEntityExtract();
//        ModelOutput2BizOutput modelOutput2BizOutput = new ModelOutput2BizOutput();
//        ModelOutput2BizOutput.initialize();
        while ((line = br.readLine()) != null) {
            String[] columns = line.trim().split(";");
            String inputSent = columns[0];
            String modelName = "spanbert";
            List<String> batchSents = new ArrayList<>();
            System.out.println(line);
            batchSents.add(inputSent);
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


//            String modifyTags = nerPipeline(session, env, maxSeqLen, inputSent, index2Tag, ageEntityExtract, modelPredictResultModify);
            String modifyTags = ModelOutput2BizOutput.transfer2String(inputSent, startLogits, endLogits);
//            List<Map<String, String>> tmpResult = ModelOutput2BizOutput.transfer2Map(inputSent, startLogits, endLogits);
            List<String> tmpList = new ArrayList<>();
            tmpList.add(columns[0]);
            tmpList.add(columns[1]);
            String[] tmpWords = columns[0].split(" ");
            String[] tmpTokenTags = modifyTags.split(" ");
            tmpList.add(modifyTags);
            List<TokenNerTag> tmpTokenNerTags = new ArrayList<>();
            for (int n = 0; n < tmpTokenTags.length; n++) {
                TokenNerTag tokenNerTag = new TokenNerTag(tmpWords[n], tmpTokenTags[n]);
                tmpTokenNerTags.add(tokenNerTag);
            }
            tmpTokenNerTags = modelOutputAnalyze.processModelOutput(tmpTokenNerTags);
            //重新转换回去
            String[] newTmpTokenTags = new String[tmpTokenTags.length];
            for (int n = 0; n < tmpTokenNerTags.size(); n++) {
                newTmpTokenTags[n] = tmpTokenNerTags.get(n).getTag();
            }
            modifyTags = StringUtils.join(newTmpTokenTags, " ");
            results.add(tmpList);
            bw.write(columns[0] + ";" + columns[1] + ";" + modifyTags + "\n");
//            bw.write(columns[0]+";"+modifyTags+"\n");
        }
        br.close();
        bw.close();
        System.out.println("DDDd");
    }
}
