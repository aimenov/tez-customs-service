package kz.ne.railways.tezcustoms.service.util;

import kz.ne.railways.tezcustoms.service.constants.Constants;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleUtils {

    public static String getTranslatedBundle(String bundleTag, String lang) {
        if (bundleTag == null) {
            return null;
        }
        ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_PATH, Locale.forLanguageTag(lang));
        return bundle.getString(bundleTag);
    }

    public static String getDefaultBundle(String bundleTag) {
        if (bundleTag == null) {
            return null;
        }
        ResourceBundle bundle = ResourceBundle.getBundle(Constants.BUNDLE_PATH, Locale.getDefault());
        return bundle.getString(bundleTag);
    }
}
