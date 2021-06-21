package eu.yeger.apollo.fol.parser

/**
 * Backing [Map] that associates a setting with its values.
 *
 * This is legacy code.
 */
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
  Settings.INFIX_PRED to listOf(),
  Settings.EQUAL_SIGN to listOf("=")
)

/**
 * Global settings [Map] used for configuring the parser.
 *
 * This is legacy code.
 */
internal object Settings : Map<String, List<String>> by settingsMap {
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
  const val EQUAL_SIGN = "equality"

  /**
   * Gets the settings associated with a key or an empty [List] if that key is not known.
   *
   * @param key The key of the settings.
   */
  override fun get(key: String) = settingsMap[key] ?: emptyList()
}
