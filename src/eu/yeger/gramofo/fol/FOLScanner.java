package eu.yeger.gramofo.fol;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the scanner for the parser class. It translates a stream of chars to a stream of tokens.
 */
public class FOLScanner {

    private String source;
    private int pos;
    private FOLToken curToken;
    private FOLToken lookAHeadToken;


    private String[] settingNames = {
            Settings.TRUE,
            Settings.FALSE,
            Settings.OR,
            Settings.AND,
            Settings.NOT,
            Settings.IMPLICATION,
            Settings.BIIMPLICATION,
            Settings.EXISTS,
            Settings.FORALL,
            Settings.INFIX_PRED,
            Settings.EQUAL_SIGN,
            Settings.INFIX_FUNC
    };

    private int[] settingTypes = {
            FOLToken.TRUE,
            FOLToken.FALSE,
            FOLToken.OR,
            FOLToken.AND,
            FOLToken.NOT,
            FOLToken.IMPLICATION,
            FOLToken.BIIMPLICATION,
            FOLToken.EXISTS,
            FOLToken.FORALL,
            FOLToken.INFIX_PRED,
            FOLToken.EQUAL_SIGN,
            FOLToken.INFIX_FUNC
    };

    private String[] allSettings;
    private HashMap<String, Integer> settingType;


    /**
     * @param source   the text which should be scanned
     * @param settings an instance from Settings-Class, which contains all Operators and Keywords
     * @throws ParseException if the settings-file is invalid or incomplete
     */
    public FOLScanner(String source, Settings settings) throws ParseException {
        extractSettings(settings);
        this.source = source;
        pos = 0;
        curToken = new FOLToken(0, "");
        lookAHeadToken = new FOLToken(0, "");
        nextToken();
        nextToken();
    }


    private void extractSettings(Settings settings) throws ParseException {
        String[] tt = (settings.getSetting(Settings.TRUE));
        String[] ff = (settings.getSetting(Settings.FALSE));
        String[] or = (settings.getSetting(Settings.OR));
        String[] and = (settings.getSetting(Settings.AND));
        String[] not = (settings.getSetting(Settings.NOT));
        String[] implication = (settings.getSetting(Settings.IMPLICATION));
        String[] biimplication = (settings.getSetting(Settings.BIIMPLICATION));
        String[] exists = (settings.getSetting(Settings.EXISTS));
        String[] forall = (settings.getSetting(Settings.FORALL));
        String[] infixPred = (settings.getSetting(Settings.INFIX_PRED));
        String[] equalSigns = (settings.getSetting(Settings.EQUAL_SIGN));
        String[] infixFunc = (settings.getSetting(Settings.INFIX_FUNC));

        String[][] settingBundle = {tt, ff, or, and, not, implication, biimplication, exists,
                forall, infixPred, equalSigns, infixFunc};

        for (int i = 0; i < settingBundle.length; i++) {
            if (settingBundle[i] == null) {
                throw new ParseException(Lang.getString("FOS_INPUT_ERROR_1", settingNames[i]));
            }
        }

        settingType = new HashMap<String, Integer>();
        ArrayList<String> temp = new ArrayList<String>();

        for (int i = 0; i < settingBundle.length; i++) {
            for (int j = 0; j < settingBundle[i].length; j++) {
                temp.add(settingBundle[i][j]);
                settingType.put(settingBundle[i][j], settingTypes[i]);
            }
        }

        allSettings = temp.toArray(new String[temp.size()]);
        sortByStringLength(allSettings);

        //check for double used signs/keywords
        for (int i = 0; i < allSettings.length - 1; i++) {
            for (int j = i + 1; j < allSettings.length; j++) {
                if (allSettings[i].equals(allSettings[j])) {
                    throw new ParseException(Lang.getString("FOS_INPUT_ERROR_2", allSettings[i]));
                }
            }
        }

    }


    /**
     * Read the next chars of the input and set curToken and lookAHeadToken to the next value.
     */
    public void nextToken() {
        FOLToken temp = curToken;
        curToken = lookAHeadToken;
        lookAHeadToken = temp;

        char next = sourceCharAt(pos);

        while (isWhiteSpace(next)) {
            if (next == 0) {
                lookAHeadToken.setTypeAndValue(FOLToken.END_OF_SOURCE, "");
                return;
            }
            pos++;
            next = sourceCharAt(pos);
        }

        identifyToken();
    }


    private void identifyToken() {

        if (sourceCharAt(pos) == '(') {
            lookAHeadToken.setTypeAndValue(FOLToken.BRACKET, "(");
            pos++;
            return;
        }

        if (sourceCharAt(pos) == ')') {
            lookAHeadToken.setTypeAndValue(FOLToken.BRACKET, ")");
            pos++;
            return;
        }

        if (sourceCharAt(pos) == '.') {
            lookAHeadToken.setTypeAndValue(FOLToken.DOT, ".");
            pos++;
            return;
        }

        if (sourceCharAt(pos) == ',') {
            lookAHeadToken.setTypeAndValue(FOLToken.COMMA, ",");
            pos++;
            return;
        }

        //all operators and keywords, which are defined by settings-file
        for (String token : allSettings) {
            if (source.startsWith(token, pos)) {
                char lastChar = token.charAt(token.length() - 1);

                char nextChar = (pos + token.length() >= source.length()) ? '\0' : source.charAt(pos + token.length());
                if (isSymbolCharacter(lastChar) && isSymbolCharacter(nextChar)) {
                    //seems to be a symbol, which start like a keyword
                    continue;
                }
                lookAHeadToken.setTypeAndValue(settingType.get(token), token);
                pos += token.length();
                return;
            }
        }

        //symbols
        if (isLetter(sourceCharAt(pos))) {
            StringBuilder sb = new StringBuilder();
            while (isSymbolCharacter(sourceCharAt(pos))) {
                sb.append(sourceCharAt(pos));
                pos++;
            }
            lookAHeadToken.setTypeAndValue(FOLToken.SYMBOL, sb.toString());
            return;
        }
        //ELSE

        // unkown char
        lookAHeadToken.setTypeAndValue(FOLToken.CHAR, String.valueOf(sourceCharAt(pos)));
        pos++;
        return;
    }


    private char sourceCharAt(int i) {
        if (pos < source.length()) {
            return source.charAt(pos);
        } else {
            return 0;
        }
    }

    private boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c) || c == 0;
    }


    private boolean isLetter(char c) {
//        return ((c >= 'a' && c <= 'z') ||
//                (c >= 'A' && c <= 'Z') ||
//                c == 'ä' ||
//                c == 'Ä' ||
//                c == 'ö' ||
//                c == 'Ö' ||
//                c == 'ü' ||
//                c == 'Ü' ||
//                c == 'ß'
//        );
        return ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z'));
    }

    private boolean isSymbolCharacter(char c) {
        return isLetter(c) ||
                c >= '0' && c <= '9' ||
                c == '_';
    }


    public String curValue() {
        return curToken.getValue();
    }

    public int curType() {
        return curToken.getType();
    }

    public String lookAHeadValue() {
        return lookAHeadToken.getValue();
    }

    public int lookAHeadType() {
        return lookAHeadToken.getType();
    }


    public String getRestOfText() {
        return " " + lookAHeadValue() + source.substring(pos);
    }

    /**
     * sort an array of string by string.length(). Longest first
     *
     * @param array
     */
    public static String[] sortByStringLength(String[] array) {

        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i].length() < array[j].length()) {
                    String temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }

        return array;
    }
}
