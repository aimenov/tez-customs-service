package kz.ne.railways.tezcustoms.service.util;

public class HtmlUtils {

    public static String html2text(String value){
        if(value == null) {
            return null;
        }
        return value
                .replaceAll("<.*?>","")
                .replaceAll("(?m)^[ \t]*\r?\n", "");
    }
}
