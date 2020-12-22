package eu.yeger.gramofo.fol

/**
 * This class is used to store the settings which are loading from a
 * configuration file.
 */
class Settings {
    // a hash for the stored settings for fast accessibility
    private val settingsCache: Map<String, Array<String>> = mapOf(
        NOT to arrayOf("!"),
        TRUE to arrayOf("tt"),
        FALSE to arrayOf("ff"),
        OR to arrayOf("|", "||"),
        AND to arrayOf("&", "&&"),
        IMPLICATION to arrayOf("->"),
        BI_IMPLICATION to arrayOf("<->"),
        EXISTS to arrayOf("exists"),
        FOR_ALL to arrayOf("forall"),
        INFIX_PRED to arrayOf(">", "<", "<=", ">=", "~"),
        INFIX_FUNC to arrayOf(),
        EQUAL_SIGN to arrayOf("=")
    )

    /**
     * Returns the value of a required settings.
     *
     * @param key The key of the setting
     * @return Returns the key if it exists, null otherwise.
     */
    fun getSetting(key: String): Array<String> {
        return settingsCache[key]!!
    }

    companion object {
        const val TRUE = "true"
        const val FALSE = "false"
        const val OR = "\u2228"
        const val AND = "\u2227"
        const val NOT = "\u00AC"
        const val IMPLICATION = "\u2192"
        const val BI_IMPLICATION = "\u2194"
        const val EXISTS = "\u2203"
        const val FOR_ALL = "\u2200"
        const val INFIX_PRED = "infix_predicates"
        const val INFIX_FUNC = "infix_functions"
        const val EQUAL_SIGN = "equality"
    }
}
