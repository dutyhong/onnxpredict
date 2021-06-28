package biz.onnx;

import ai.onnxruntime.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public class Test {
    private static final Logger logger = Logger.getLogger(Test.class.getName());

    private static void writeData(float[][][][] data, int[] indices, float[] values) {
//        zeroData(data);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                for (int k = 0; k < data[i][j].length; k++) {
                    Arrays.fill(data[i][j][k], 0.0f);
                }
            }
        }
        for (int m = 0; m < indices.length; m++) {
            int i = (indices[m]) / 28;
            int j = (indices[m]) % 28;
            data[0][0][i][j] = values[m] / 255;
        }

        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                data[0][0][i][j] = (data[0][0][i][j] - 0.1307f) / 0.3081f;
            }
        }
    }

    public static void main(String[] args) throws OrtException, IOException {
        String filePath = "/Users/duty/Downloads/cnn_mnist_pytorch.onnx";

        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();

        opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);

        logger.info("Loading model from " + filePath);
        OrtSession session = env.createSession(filePath, opts);

        logger.info("Inputs:");
        for (NodeInfo i : session.getInputInfo().values()) {
            logger.info(i.toString());
        }

        logger.info("Outputs:");
        for (NodeInfo i : session.getOutputInfo().values()) {
            logger.info(i.toString());
        }


        SparseData sparseData = SparseData.load("/Users/duty/Downloads/mnist.t");
        float[][][][] testData = new float[1][1][28][28];
        String inputName = session.getInputNames().iterator().next();
        int cnt = 0;
        for (int i = 0; i < sparseData.labels.length; i++) {
            cnt++;
            if (cnt > 10)
                break;
            writeData(testData, sparseData.indices.get(i), sparseData.values.get(i));

            OnnxTensor testTensor = OnnxTensor.createTensor(env, testData);
            OrtSession.Result result = session.run(Collections.singletonMap(inputName, testTensor));
            float[][] outprobs = (float[][]) result.get(0).getValue();  //一个样本预测
            System.out.println(Arrays.toString(outprobs[0])); // 类别logits
        }
    }
}