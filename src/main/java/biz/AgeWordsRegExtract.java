package biz;

import edu.stanford.nlp.util.StringUtils;
import util.NumericWordUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgeWordsRegExtract {
    //根据规则匹配 找到year month day前的第一个数字，找到age后的最后一个数字所组成的词则为年龄词组
    public static String ageWordsRegExtract(String inputSent) {
        if (null == inputSent)
            return null;
        String[] words = inputSent.split(" ");
        int length = words.length;
        if (length <= 1)
            return null;
        List<String> wordsList = Arrays.asList(words);
        String[] conditionWords = {"year", "month", "day", "age", "years", "months", "days"};
        List<String> conditionWordsList = Arrays.asList(conditionWords);
        if (!wordsList.contains("year") && !wordsList.contains("years") && !wordsList.contains("months") && !wordsList.contains("month") && !wordsList.contains("day") && !wordsList.contains("days") && !wordsList.contains("age"))
            return null;
        List<String> subList = new ArrayList<>();
        int tmpLastIndex = 0;
        String tmpAgeStr = "";
        for (int i = 0; i < length; i++) {
            if (conditionWordsList.contains(words[i])) {
                if (!"age".equals(words[i])) {
                    //找到前面的第一个数字
                    subList = wordsList.subList(0, i);
                    int firstDigitIndex = findFirstDigit(subList);
                    if (firstDigitIndex == -1) {
                        return null;
                    } else {
                        tmpAgeStr = StringUtils.join(wordsList.subList(firstDigitIndex, i + 1));
                        tmpLastIndex = i;
                    }
                } else {
                    //找到后面最后一个数字
                    subList = wordsList.subList(i + 1, length);
                    int lastDigitIndex = findLastDigit(subList);
                    if (lastDigitIndex == -1) {
                        return null;
                    } else {
                        tmpAgeStr = StringUtils.join(wordsList.subList(i, i + lastDigitIndex + 2));
                        tmpLastIndex = i + lastDigitIndex + 1;
                    }
                }
            }
        }
        if (tmpLastIndex < length - 1 && tmpLastIndex != 0) {
            if ("old".equals(wordsList.get(tmpLastIndex + 1)))
                tmpAgeStr = tmpAgeStr + " old";
        }
        return tmpAgeStr;
    }

    public static int findFirstDigit(List<String> subList) {
        int size = subList.size();
        for (int i = 0; i < size; i++) {
            String word = subList.get(i);
            if (NumericWordUtil.isNumeric(word)) {
                return i;
            }
        }
        return -1;
    }

    public static int findLastDigit(List<String> subList) {
        int size = subList.size();
        for (int i = size - 1; i >= 0; i--) {
            String word = subList.get(i);
            if (NumericWordUtil.isNumeric(word)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        String[] texts = {"girls' dresses age 5", "6~14 year girl clothes", "3 6 year t shirt", "boy clothes age 6 9"};
//        AgeWordsRegExtract ageWordsRegExtract = new AgeWordsRegExtract();
        for (int i = 0; i < texts.length; i++) {
            String res = AgeWordsRegExtract.ageWordsRegExtract(texts[i]);
            System.out.println(res);
        }
    }
}
