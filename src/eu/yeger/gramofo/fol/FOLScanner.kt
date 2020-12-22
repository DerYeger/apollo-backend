package eu.yeger.gramofo.fol

import eu.yeger.gramofo.fol.Lang.getString
import java.util.*

/**
 * This is the scanner for the parser class. It translates a stream of chars to a stream of tokens.
 */
class FOLScanner(private val source: String, settings: Settings) {
    private var pos: Int
    private var curToken: FOLToken
    private var lookAHeadToken: FOLToken
    private val settingNames = arrayOf(
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
    )
    private val settingTypes = intArrayOf(
        FOLToken.TRUE,
        FOLToken.FALSE,
        FOLToken.OR,
        FOLToken.AND,
        FOLToken.NOT,
        FOLToken.IMPLICATION,
        FOLToken.BI_IMPLICATION,
        FOLToken.EXISTS,
        FOLToken.FOR_ALL,
        FOLToken.INFIX_PRED,
        FOLToken.EQUAL_SIGN,
        FOLToken.INFIX_FUNC
    )
    private lateinit var allSettings: Array<String>
    private var settingType: HashMap<String, Int>? = null

    init {
        extractSettings(settings)
        pos = 0
        curToken = FOLToken(0, "")
        lookAHeadToken = FOLToken(0, "")
        nextToken()
        nextToken()
    }

    @Throws(ParseException::class)
    private fun extractSettings(settings: Settings) {
        val tt = settings.getSetting(Settings.TRUE)
        val ff = settings.getSetting(Settings.FALSE)
        val or = settings.getSetting(Settings.OR)
        val and = settings.getSetting(Settings.AND)
        val not = settings.getSetting(Settings.NOT)
        val implication = settings.getSetting(Settings.IMPLICATION)
        val biImplication = settings.getSetting(Settings.BIIMPLICATION)
        val exists = settings.getSetting(Settings.EXISTS)
        val forAll = settings.getSetting(Settings.FORALL)
        val infixPred = settings.getSetting(Settings.INFIX_PRED)
        val equalSigns = settings.getSetting(Settings.EQUAL_SIGN)
        val infixFunc = settings.getSetting(Settings.INFIX_FUNC)
        val settingBundle = arrayOf<Array<String>?>(
            tt, ff, or, and, not, implication, biImplication, exists,
            forAll, infixPred, equalSigns, infixFunc
        )
        for (i in settingBundle.indices) {
            if (settingBundle[i] == null) {
                throw ParseException(getString("FOS_MISSING_SETTING", settingNames[i]))
            }
        }
        settingType = HashMap()
        val temp = ArrayList<String>()
        for (i in settingBundle.indices) {
            for (j in settingBundle[i]!!.indices) {
                temp.add(settingBundle[i]!![j])
                settingType!![settingBundle[i]!![j]] = settingTypes[i]
            }
        }
        allSettings = temp.toTypedArray()
        sortByStringLength(allSettings)

        // check for double used signs/keywords
        for (i in 0 until allSettings.size - 1) {
            for (j in i + 1 until allSettings.size) {
                if (allSettings[i] == allSettings[j]) {
                    throw ParseException(getString("FOS_DUPLICATE_SETTING", allSettings[i]))
                }
            }
        }
    }

    /**
     * Read the next chars of the input and set curToken and lookAHeadToken to the next value.
     */
    fun nextToken() {
        val temp = curToken
        curToken = lookAHeadToken
        lookAHeadToken = temp
        var next = sourceCharAt(pos)
        while (isWhiteSpace(next)) {
            if (next.toInt() == 0) {
                lookAHeadToken.setTypeAndValue(FOLToken.END_OF_SOURCE, "")
                return
            }
            pos++
            next = sourceCharAt(pos)
        }
        identifyToken()
    }

    private fun identifyToken() {
        if (sourceCharAt(pos) == '(') {
            lookAHeadToken.setTypeAndValue(FOLToken.BRACKET, "(")
            pos++
            return
        }
        if (sourceCharAt(pos) == ')') {
            lookAHeadToken.setTypeAndValue(FOLToken.BRACKET, ")")
            pos++
            return
        }
        if (sourceCharAt(pos) == '.') {
            lookAHeadToken.setTypeAndValue(FOLToken.DOT, ".")
            pos++
            return
        }
        if (sourceCharAt(pos) == ',') {
            lookAHeadToken.setTypeAndValue(FOLToken.COMMA, ",")
            pos++
            return
        }

        // all operators and keywords, which are defined by settings-file
        for (token in allSettings) {
            if (source.startsWith(token, pos)) {
                val lastChar = token[token.length - 1]
                val nextChar = if (pos + token.length >= source.length) '\u0000' else source[pos + token.length]
                if (isSymbolCharacter(lastChar) && isSymbolCharacter(nextChar)) {
                    // seems to be a symbol, which start like a keyword
                    continue
                }
                lookAHeadToken.setTypeAndValue(settingType!![token]!!, token)
                pos += token.length
                return
            }
        }

        // symbols
        if (isLetter(sourceCharAt(pos))) {
            val sb = StringBuilder()
            while (isSymbolCharacter(sourceCharAt(pos))) {
                sb.append(sourceCharAt(pos))
                pos++
            }
            lookAHeadToken.setTypeAndValue(FOLToken.SYMBOL, sb.toString())
            return
        }
        // ELSE

        // unknown char
        lookAHeadToken.setTypeAndValue(FOLToken.CHAR, sourceCharAt(pos).toString())
        pos++
        return
    }

    private fun sourceCharAt(index: Int): Char {
        return if (0 <= index && index < source.length) {
            source[index]
        } else {
            Char.MIN_VALUE
        }
    }

    private fun isWhiteSpace(c: Char): Boolean {
        return Character.isWhitespace(c) || c.toInt() == 0
    }

    private fun isLetter(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z'
    }

    private fun isSymbolCharacter(c: Char): Boolean {
        return isLetter(c) || c in '0'..'9' || c == '_'
    }

    fun curValue(): String {
        return curToken.value
    }

    fun curType(): Int {
        return curToken.type
    }

    private fun lookAHeadValue(): String {
        return lookAHeadToken.value
    }

    val restOfText: String
        get() = " " + lookAHeadValue() + source.substring(pos)

    companion object {
        /**
         * sort an array of string by string.length(). Longest first
         *
         * @param array
         */
        fun sortByStringLength(array: Array<String>): Array<String> {
            for (i in 0 until array.size - 1) {
                for (j in i + 1 until array.size) {
                    if (array[i].length < array[j].length) {
                        val temp = array[i]
                        array[i] = array[j]
                        array[j] = temp
                    }
                }
            }
            return array
        }
    }
}
