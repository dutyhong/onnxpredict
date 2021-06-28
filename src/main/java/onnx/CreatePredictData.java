package onnx;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

import java.util.*;

public class CreatePredictData {
    /**
     * 输出一个batch的java onnx的输入数据
     *
     * @param batchOutput
     * @param env
     * @param session
     * @return
     * @throws Exception
     */
    public static List<Map<String, OnnxTensor>> multiPredictData(List<List<long[][]>> batchOutput, OrtEnvironment env, OrtSession session) throws Exception {
        List<Map<String, OnnxTensor>> predictData = new ArrayList<>();
        for (int i = 0; i < batchOutput.size(); i++) {
            List<long[][]> firstSent = batchOutput.get(i);
            long[][] inputIds = firstSent.get(0);
            long[][] attentionMasks = firstSent.get(1);
            long[][] tokenTypeIds = firstSent.get(2);
            long[][] sentSlotsIds = firstSent.get(3);

            //查看输入名称
            Iterator<String> inputNamesIterator = session.getInputNames().iterator();
            List<String> inputNames = new ArrayList<>();
            while (inputNamesIterator.hasNext()) {
                String it = inputNamesIterator.next();
                inputNames.add(it);
                System.out.println(it);
            }

            //构造java onnx输入数据类型
            OnnxTensor testTensor1 = OnnxTensor.createTensor(env, inputIds);
            OnnxTensor testTensor2 = OnnxTensor.createTensor(env, attentionMasks);
            OnnxTensor testTensor3 = OnnxTensor.createTensor(env, tokenTypeIds);
            OnnxTensor testTensor4 = OnnxTensor.createTensor(env, sentSlotsIds);

            Map<String, OnnxTensor> inputsTensors = new HashMap<>();
            inputsTensors.put(inputNames.get(0), testTensor1);
            inputsTensors.put(inputNames.get(1), testTensor2);
            inputsTensors.put(inputNames.get(2), testTensor3);
            inputsTensors.put(inputNames.get(3), testTensor4);
            predictData.add(inputsTensors);
        }
        return predictData;
    }

    /**
     * 输出一个batch的java onnx的输入数据
     *
     * @param batchOutput
     * @param env
     * @param session
     * @return
     * @throws Exception
     */
    public static Map<String, OnnxTensor> singlePredictData(List<List<long[][]>> batchOutput, OrtEnvironment env, OrtSession session, int index, String modelTag) throws Exception {
        if (index < 0 || index >= batchOutput.size()) {
            System.out.println("输入的所以不合理 index out of range !!");
            return null;
        }
        List<long[][]> firstSent = batchOutput.get(index);
        long[][] inputIds = firstSent.get(0);
        long[][] attentionMasks = firstSent.get(1);
        long[][] tokenTypeIds = firstSent.get(2);
        long[][] sentSlotsIds1 = firstSent.get(3);
        long[][] sentSlotsIds2 = firstSent.get(4);

        //查看输入名称
        Iterator<String> inputNamesIterator = session.getInputNames().iterator();
        List<String> inputNames = new ArrayList<>();
        while (inputNamesIterator.hasNext()) {
            String it = inputNamesIterator.next();
            inputNames.add(it);
            System.out.println(it);
        }

        //构造java onnx输入数据类型
        OnnxTensor testTensor1 = OnnxTensor.createTensor(env, inputIds);
        OnnxTensor testTensor2 = OnnxTensor.createTensor(env, attentionMasks);
        OnnxTensor testTensor3 = OnnxTensor.createTensor(env, tokenTypeIds);
        OnnxTensor testTensor4 = OnnxTensor.createTensor(env, sentSlotsIds1);
        OnnxTensor testTensor5 = OnnxTensor.createTensor(env, sentSlotsIds2);
        Map<String, OnnxTensor> inputsTensors = new HashMap<>();

        if (modelTag.equals("bert")) {
            inputsTensors.put(inputNames.get(0), testTensor1);
            inputsTensors.put(inputNames.get(1), testTensor2);
            inputsTensors.put(inputNames.get(2), testTensor3);
            inputsTensors.put(inputNames.get(3), testTensor4);
        } else if (modelTag.equals("spanbert")) {
            inputsTensors.put(inputNames.get(0), testTensor1);
            inputsTensors.put(inputNames.get(1), testTensor2);
            inputsTensors.put(inputNames.get(2), testTensor3);
//                    inputsTensors.put(inputNames.get(3), testTensor4);
//                    inputsTensors.put(inputNames.get(4), testTensor5);
        } else {
            System.out.println("没有满足条件的模型输入！！请查看当前模型");
        }
        return inputsTensors;

    }


}
