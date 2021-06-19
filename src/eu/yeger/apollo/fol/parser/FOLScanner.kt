package eu.yeger.apollo.fol.parser

/**
 * This is the scanner of the [FOLParser].
 * It translates a stream of chars to a stream of tokens.
 *
 * This is legacy code.
 *
 * @property source The source [String] representing a formula.
 * @property language The [Language] used for translating error messages.
 * @constructor Creates a [FOLScanner] with the given [source] and [language].
 */
internal class FOLScanner(private val source: String, private val language: Language) {
    private var pos: Int = 0
    private var curToken: FOLToken = FOLToken(0, "")
    private var lookAHeadToken: FOLToken = FOLToken(0, "")
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
        FOLToken.EQUAL_SIGN
    )
    private lateinit var allSettings: Array<String>
    private val settingType: HashMap<String, Int> = HashMap()

    init {
        extractSettings()
        nextToken()
        nextToken()
    }

    @Throws(ParseException::class)
    private fun extractSettings() {
        val tt = Settings[Settings.TRUE]
        val ff = Settings[Settings.FALSE]
        val or = Settings[Settings.OR]
        val and = Settings[Settings.AND]
        val not = Settings[Settings.NOT]
        val implication = Settings[Settings.IMPLICATION]
        val biImplication = Settings[Settings.BI_IMPLICATION]
        val exists = Settings[Settings.EXISTS]
        val forAll = Settings[Settings.FOR_ALL]
        val infixPred = Settings[Settings.INFIX_PRED]
        val equalSigns = Settings[Settings.EQUAL_SIGN]
        val settingBundle = arrayOf(
            tt, ff, or, and, not, implication, biImplication, exists,
            forAll, infixPred, equalSigns
        )
        val temp = ArrayList<String>()
        for (i in settingBundle.indices) {
            for (j in settingBundle[i].indices) {
                temp.add(settingBundle[i][j])
                settingType[settingBundle[i][j]] = settingTypes[i]
            }
        }
        allSettings = temp.toTypedArray()
        sortByStringLength(allSettings)

        // check for double used signs/keywords
        for (i in 0 until allSettings.size - 1) {
            for (j in i + 1 until allSettings.size) {
                if (allSettings[i] == allSettings[j]) {
                    throw ParseException(language.getString("FOS_DUPLICATE_SETTING", allSettings[i]))
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
            if (next.code == 0) {
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
                lookAHeadToken.setTypeAndValue(settingType[token]!!, token)
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
        return Character.isWhitespace(c) || c.code == 0
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
