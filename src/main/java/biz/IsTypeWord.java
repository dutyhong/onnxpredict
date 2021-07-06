package biz;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tizi,muzhong
 * @date 2021/7/2
 */
public class IsTypeWord {
    private static final Pattern p1 = Pattern.compile("\\d+");
    //型号词正则匹配：肯定包含数字
    public static boolean digitWord(String word) {
        Matcher match = p1.matcher(word);
        if (match.find()) {
            return true;
        } else {
            return false;
        }
    }

    //
    public static boolean isTypeWords(List<String> words) {
        int size = words.size();
        int[] allFlags = new int[size];
        int oneFlags = 0;
        for (int i = 0; i < size; i++) {
            if (digitWord(words.get(i)) || RuleDataLoad.isType(words.get(i))) {
                allFlags[i] = 1;
                if (RuleDataLoad.isType(words.get(i))) {
                    oneFlags = 1;
                }
            } else {
                allFlags[i] = 0;
            }

        }
        for (int i = 0; i < size; i++) {
            if (allFlags[i] == 0) {
                return false;
            }
        }
        if (oneFlags == 0) {
            return false;
        }
        return true;
    }
}
