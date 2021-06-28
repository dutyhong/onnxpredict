package util;

import java.util.regex.Pattern;

/**
 * @author tizi,muzhong
 */
public class NumericWordUtil {
    private static final Pattern pattern = Pattern.compile("[0-9]+");
    public static boolean isNumeric(String word) {
        return pattern.matcher(word).matches();
    }

    public static void main(String[] args) {
        String tmp = "89";
        boolean res = NumericWordUtil.isNumeric(tmp);
        System.out.println(res);
    }
}
