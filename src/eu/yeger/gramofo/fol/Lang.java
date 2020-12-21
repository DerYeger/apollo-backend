package eu.yeger.gramofo.fol;

import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Used to simplify internationalization.
 */
public class Lang {

    // property file is: package/name/messages.properties
    private static ResourceBundle resourceBundle = loadBundle("lang", "language", Locale.getDefault());

    /**
     * Loads a new ResourceBundle by using the given local.
     *
     * @param locale
     */
    public static void changeLocale(Locale locale) {
        resourceBundle = loadBundle("lang", "language", locale);
    }

    /**
     * Get a internationalized string for the given key
     *
     * @param key the key to get a associated string for.
     * @return a internationalized string
     */
    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return '[' + key + ']';
        }
    }

    /**
     * Get a internationalized string for the given key
     *
     * @param key  the key to get a associated string for.
     * @param args a list of object, which should be integrated in the string. Which and how many arguments are needed
     *             is specified by the string itself
     * @return a internationalized string
     */
    public static String getString(String key, Object... args) {
        try {
            return MessageFormat.format(resourceBundle.getString(key), args);
        } catch (MissingResourceException e) {
            return '[' + key + ']';
        }
    }

    public static ResourceBundle loadBundle(String bundleName, String relativePath, Locale locale) {
        try {
            return ResourceBundle.getBundle(relativePath + "." + bundleName, locale);
        } catch (Exception e) {
            System.out.println(e);
            System.err.println("\nCouldn't find language-files on path '" + relativePath + "'");
            return new ListResourceBundle() {
                @Override
                protected Object[][] getContents() {
                    return new Object[][]{};
                }
            };
        }
    }

    public static ResourceBundle loadBundle(String bundleName, String relativePath) {
        return loadBundle(bundleName, relativePath, Locale.getDefault());
    }

}
