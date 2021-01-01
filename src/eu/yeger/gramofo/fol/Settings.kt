package eu.yeger.gramofo.fol

private val settingsMap: Map<String, List<String>> = mapOf(
    Settings.NOT to listOf("!"),
    Settings.TRUE to listOf("tt"),
    Settings.FALSE to listOf("ff"),
    Settings.OR to listOf("|", "||"),
    Settings.AND to listOf("&", "&&"),
    Settings.IMPLICATION to listOf("->"),
    Settings.BI_IMPLICATION to listOf("<->"),
    Settings.EXISTS to listOf("exists"),
    Settings.FOR_ALL to listOf("forall"),
    Settings.INFIX_PRED to listOf(">", "<", "<=", ">=", "~"),
    Settings.INFIX_FUNC to listOf(),
    Settings.EQUAL_SIGN to listOf("=")
)

/**
 * This class is used to store the settings which are loading from a
 * configuration file.
 */
object Settings : Map<String, List<String>> by settingsMap {
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

    override fun get(key: String) = settingsMap[key] ?: emptyList()
}
