package biz;

import com.fordeal.search.stellaris.nova.KVStore;

import java.util.Arrays;

/**
 * @author muzhong
 * @date 2021/7/2
 */
public class RuleDataLoad {

    private static KVStore currentNerWordTypeDict;

    public static void init(KVStore nerWordTypeDict) {
        currentNerWordTypeDict = nerWordTypeDict;
    }

    private static String getCachedFromGuava(String inputStr) {
        try {
            if (currentNerWordTypeDict == null || inputStr == null || inputStr.trim().isEmpty()) {
                return null;
            }
            return (String) currentNerWordTypeDict.get(inputStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * https://wiki.duolainc.com/pages/viewpage.action?pageId=39199080
     * 检查ner词库分词，该inputStr 是否是 typeName 类型的词
     * @param inputStr
     * @param typeName
     * @return
     */
    public static boolean checkWordType(String inputStr, String typeName) {
        String cached = getCachedFromGuava(inputStr);
        if (cached != null || !cached.isEmpty()) {
            if ("brand".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "1".equals(x.trim()));
            } else if ("category".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "2".equals(x.trim()));
            }  else if ("property".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "3".equals(x.trim()));
            } else if ("population".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "4".equals(x.trim()));
            } else if ("phrase".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "5".equals(x.trim()));
            } else if ("modifier".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "7".equals(x.trim()));
            } else if ("color".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "8".equals(x.trim()));
            } else if ("style".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "9".equals(x.trim()));
            } else if ("scene".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "10".equals(x.trim()));
            } else if ("function".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "11".equals(x.trim()));
            } else if ("size".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "12".equals(x.trim()));
            } else if ("type".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "13".equals(x.trim()));
            } else if ("catemodifier".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "14".equals(x.trim()));
            } else if ("material".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "15".equals(x.trim()));
            } else if ("element".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "16".equals(x.trim()));
            } else if ("age".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "17".equals(x.trim()));
            } else if ("ip".equalsIgnoreCase(typeName)) {
                return Arrays.asList(cached.split(",")).stream().anyMatch(x -> "18".equals(x.trim()));
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isElementWord(String inputStr) {
        return checkWordType(inputStr, "element");
    }

    public static boolean isStyle(String inputStr) {
        return checkWordType(inputStr, "style");
    }

    public static boolean isBeCategory(String inputStr) {
        return checkWordType(inputStr, "category");
    }

    public static boolean isCateModifier(String inputStr) {
        return checkWordType(inputStr, "catemodifier");
    }

    public static boolean isType(String inputStr) {
        return checkWordType(inputStr, "type");
    }

    public static boolean isBrand(String inputStr) {
        return checkWordType(inputStr, "brand");
    }

    public static boolean isIp(String inputStr) {
        return checkWordType(inputStr, "ip");
    }

    public static boolean isPopulation(String inputStr) {
        return checkWordType(inputStr, "population");
    }

    public static boolean isFunction(String inputStr) {
        return checkWordType(inputStr, "function");
    }

}
