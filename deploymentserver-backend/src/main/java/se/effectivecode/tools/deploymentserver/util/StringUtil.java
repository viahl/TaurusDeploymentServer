package se.effectivecode.tools.deploymentserver.util;

public class StringUtil {

    public static boolean isNotEmpty(String str) {
        return isNotNull(str) && !isEmpty(str);
    }

    public static boolean isEmpty(String str) {
        return isNotNull(str) && str.isEmpty();
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isNull(String str) {
        return str == null;
    }
}
