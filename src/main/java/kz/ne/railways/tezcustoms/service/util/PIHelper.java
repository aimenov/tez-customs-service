package kz.ne.railways.tezcustoms.service.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PIHelper {
    public static String getNotNullVal(Object v) {
        return (v != null) ? v.toString() : "";
    }

    public static Long getLongVal(String value) {
        Long result = null;
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return result;
        }
    }

    public static Long getLongVal(Object value) {
        Long result = null;
        if (value == null)
            return result;
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return result;
        }
    }

    public static Integer getIntVal(Object value) {
        Integer result = null;
        if (value == null)
            return result;
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return result;
        }
    }

    public static Short getShortVal(Object value) {
        Short result = null;
        try {
            return Short.parseShort(value.toString());
        } catch (Exception e) {
            return result;
        }
    }

    public static BigDecimal getBigDecimalVal(Object value) {
        BigDecimal result = null;
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            String pattern = "#,##0.0#";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);

            // parse the string
            return (BigDecimal) decimalFormat.parse(value.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static String getStringVal(Object value) {
        String result = null;
        try {
            return value.toString();
        } catch (Exception e) {
            return result;
        }
    }

    public static boolean like(String str, String expr) {
        return str.toLowerCase().contains(expr.toLowerCase());
    }

    public static String parseXmlToString(String xml, String delimeter) {
        if (xml == null)
            return null;
        if (StringUtils.isBlank(xml.replace("<A/>", ""))) {
            return "";
        }
        xml = xml.replace("</A><A>", delimeter);
        xml = xml.replace("<A>", "");
        xml = xml.replace("</A>", "");
        xml = xml.replaceAll("<A/>", "");
        Set<String> set = new HashSet<>(Arrays.asList(xml.split(delimeter)));
        return StringUtils.join(set, delimeter);
    }
}
