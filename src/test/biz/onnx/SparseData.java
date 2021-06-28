package biz.onnx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author tizi
 */
public class SparseData {
    private static final Logger logger = Logger.getLogger(SparseData.class.getName());
    private static final Pattern splitPattern = Pattern.compile("\\s+");

    public final int[] labels;
    public final List<int[]> indices;
    public final List<float[]> values;

    public SparseData(int[] labels, List<int[]> indices, List<float[]> values) {
        this.labels = labels;
        this.indices = Collections.unmodifiableList(indices);
        this.values = Collections.unmodifiableList(values);
    }

    private static int[] convertInts(List<Integer> list) {
        int[] output = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            output[i] = list.get(i);
        }
        return output;
    }

    private static float[] convertFloats(List<Float> list) {
        float[] output = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            output[i] = list.get(i);
        }
        return output;
    }

    public static SparseData load(String path) throws IOException {
        int pos = 0;
        List<int[]> indices = new ArrayList<>();
        List<float[]> values = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        String line;
        int maxFeatureID = Integer.MIN_VALUE;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            for (; ; ) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                pos++;
                String[] fields = splitPattern.split(line);
                int lastID = -1;
                try {
                    boolean valid = true;
                    List<Integer> curIndices = new ArrayList<>();
                    List<Float> curValues = new ArrayList<>();
                    for (int i = 1; i < fields.length && valid; i++) {
                        int ind = fields[i].indexOf(':');
                        if (ind < 0) {
                            logger.warning(String.format("Weird line at %d", pos));
                            valid = false;
                        }
                        String ids = fields[i].substring(0, ind);
                        int id = Integer.parseInt(ids);
                        curIndices.add(id);
                        if (maxFeatureID < id) {
                            maxFeatureID = id;
                        }
                        float val = Float.parseFloat(fields[i].substring(ind + 1));
                        curValues.add(val);
                        if (id <= lastID) {
                            logger.warning(String.format("Repeated features at line %d", pos));
                            valid = false;
                        } else {
                            lastID = id;
                        }
                    }
                    if (valid) {
                        // Store the label
                        labels.add(Integer.parseInt(fields[0]));
                        // Store the features
                        indices.add(convertInts(curIndices));
                        values.add(convertFloats(curValues));
                    } else {
                        throw new IOException("Invalid LibSVM format file at line " + pos);
                    }
                } catch (NumberFormatException ex) {
                    logger.warning(String.format("Weird line at %d", pos));
                    throw new IOException("Invalid LibSVM format file", ex);
                }
            }
        }

        logger.info(
                "Loaded "
                        + maxFeatureID
                        + " features, "
                        + labels.size()
                        + " samples, from + '"
                        + path
                        + "'.");
        return new SparseData(convertInts(labels), indices, values);
    }

    public static void main(String[] args) throws IOException {
        SparseData sparseData = SparseData.load("/Users/duty/Downloads/mnist.t");
        System.out.println("ddd");
    }
}
