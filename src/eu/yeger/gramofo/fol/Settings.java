package eu.yeger.gramofo.fol;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is used to store the settings which are loading from a
 * configuration file.
 */
public class Settings {

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String OR = "\u2228";
    public static final String AND = "\u2227";
    public static final String NOT = "\u00AC";
    public static final String IMPLICATION = "\u2192";
    public static final String BIIMPLICATION = "\u2194";
    public static final String EXISTS = "\u2203";
    public static final String FORALL = "\u2200";
    public static final String INFIX_PRED = "infix_predicates";
    public static final String INFIX_FUNC = "infix_functions";
    public static final String EQUAL_SIGN = "equality";

    // a hash for the stored settings for fast accessibility
    private HashMap<String, String[]> settingsCache;
    private String errorMessage;

    /**
     * Creates a new instance of settings with the specified settings file.
     *
     * @param file The file, where to load the settings
     */
    public Settings(String file) {
        this.settingsCache = new HashMap<String, String[]>();
        settingsCache.put(NOT, new String[]{"!"});
        settingsCache.put(TRUE, new String[]{"tt"});
        settingsCache.put(FALSE, new String[]{"ff"});
        settingsCache.put(OR, new String[]{"|", "||"});
        settingsCache.put(AND, new String[]{"&", "&&"});
        settingsCache.put(IMPLICATION, new String[]{"->"});
        settingsCache.put(BIIMPLICATION, new String[]{"<->"});
        settingsCache.put(EXISTS, new String[]{"exists"});
        settingsCache.put(FORALL, new String[]{"forall"});
        settingsCache.put(INFIX_PRED, new String[]{">", "<", "<=", ">=", "~"});
        settingsCache.put(INFIX_FUNC, new String[]{});
        settingsCache.put(EQUAL_SIGN, new String[]{"="});

        ///TODO Fix.
//        loadSettings(file);
    }

    /**
     * Private function to initialize loading of the settings from specified
     * file.
     *
     * @param fileName The file name.
     */
    private void loadSettings(String fileName) {
        errorMessage = null;
        Scanner scanner = null;
        int lineNumber = 1;

        try {
            Class<FOLParser> aClass = FOLParser.class;
            URL resource = getClass().getClassLoader().getResource(fileName);
            System.out.println(resource);
            File file = new File(resource.getFile());

            scanner = new Scanner(file, "UTF-8");
        } catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
            errorMessage = Lang.getString("SET_ERROR_MSG_1", fileName);
            return;
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();
            line = line.replaceAll(" ", "");

            if (!line.startsWith("#") && line.length() > 0) {
                parseSetting(line, fileName, lineNumber);
            }

            lineNumber++;
        }

        scanner.close();

        for (Map.Entry<String, String[]> stringEntry : settingsCache.entrySet()) {
            System.out.println(stringEntry.getKey());
            System.out.println(Arrays.toString(stringEntry.getValue()));
            System.out.println("");
        }
    }

    /**
     * Parses a setting of one line
     *
     * @param line       The scanned line
     * @param fileName   The fileName for error hints
     * @param lineNumber The line number for error hints.
     */
    private void parseSetting(String line, String fileName, int lineNumber) {


        String[] split = line.split(":=");
        if (split.length == 2) {

            split[0] = split[0].trim().replaceAll("\\s", "");
            split[1] = split[1].trim().replaceAll("\\s", "");

            boolean hasForbiddenChar =
                    split[1].contains("(") ||
                            split[1].contains(")") ||
                            split[1].contains(".") ||
                            split[1].isEmpty();

            boolean hasUpperCase = hasUpperCase(split[1]);

            if (!hasForbiddenChar && !hasUpperCase) {
                settingsCache.put(split[0], split[1].split(","));
            } else if (hasForbiddenChar) {
                errorMessage = Lang.getString("SET_ERROR_MSG_2", fileName, lineNumber);
            } else {
                errorMessage = Lang.getString("SET_ERROR_MSG_3", fileName, lineNumber);
            }

        } else {
            errorMessage = Lang.getString("SET_ERROR_MSG_4", fileName, lineNumber);
        }
    }


    /**
     * @param string
     * @return false if there is no upper-case-letter <br>
     * true if there is at least one upper-case-letter
     */
    private boolean hasUpperCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) >= 'A' && string.charAt(i) <= 'Z') {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if the hash contains a value for the required setting.
     *
     * @param key The key of the setting.
     * @return True, if the setting exists.
     */
    public boolean hasSetting(String key) {
        return settingsCache.containsKey(key);
    }

    /**
     * Returns the value of a required settings.
     *
     * @param key The key of the setting
     * @return Returns the key if it exists, null otherwise.
     */
    public String[] getSetting(String key) {
        return settingsCache.get(key);
    }


    /**
     * @return a message if something went wrong null else
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
