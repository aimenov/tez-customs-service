package kz.ne.railways.tezcustoms.service.util;

import java.util.List;

public class StringUtils {

    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String merge(List<String> list, String delim) {
        StringBuilder sb = new StringBuilder();
        boolean can = false;
        for (String text : list) {
            if (isEmpty(text)) continue;
            if (can) sb.append(delim);
            sb.append(text);
            can = true;
        }
        return sb.toString();
    }

}
