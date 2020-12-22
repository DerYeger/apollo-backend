package eu.yeger.gramofo.fol

import java.util.HashMap

/**
 * This class is used to store the settings which are loading from a
 * configuration file.
 */
class Settings {
    // a hash for the stored settings for fast accessibility
    private val settingsCache: HashMap<String, Array<String>> = HashMap()

    /**
     * @return a message if something went wrong null else
     */
    val errorMessage: String? = null

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
        const val BIIMPLICATION = "\u2194"
        const val EXISTS = "\u2203"
        const val FORALL = "\u2200"
        const val INFIX_PRED = "infix_predicates"
        const val INFIX_FUNC = "infix_functions"
        const val EQUAL_SIGN = "equality"
    }

    /**
     * Creates a new instance of settings with the specified settings file.
     *
     * @param file The file, where to load the settings
     */
    init {
        settingsCache[NOT] = arrayOf("!")
        settingsCache[TRUE] = arrayOf("tt")
        settingsCache[FALSE] = arrayOf("ff")
        settingsCache[OR] = arrayOf("|", "||")
        settingsCache[AND] = arrayOf("&", "&&")
        settingsCache[IMPLICATION] = arrayOf("->")
        settingsCache[BIIMPLICATION] = arrayOf("<->")
        settingsCache[EXISTS] = arrayOf("exists")
        settingsCache[FORALL] = arrayOf("forall")
        settingsCache[INFIX_PRED] = arrayOf(">", "<", "<=", ">=", "~")
        settingsCache[INFIX_FUNC] = arrayOf()
        settingsCache[EQUAL_SIGN] = arrayOf("=")
    }
}
