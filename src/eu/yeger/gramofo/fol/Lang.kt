package eu.yeger.gramofo.fol

import org.slf4j.LoggerFactory
import java.lang.Exception
import java.text.MessageFormat
import java.util.ListResourceBundle
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle
import kotlin.jvm.JvmOverloads

/**
 * Used to simplify internationalization.
 */
class Lang(locale: Locale) {
    private val resourceBundle = loadBundle("lang", "language", locale)

    /**
     * Get a internationalized string for the given key
     *
     * @param key the key to get a associated string for.
     * @return a internationalized string
     */
    fun getString(key: String): String {
        return try {
            resourceBundle.getString(key)
        } catch (e: MissingResourceException) {
            "[$key]"
        }
    }

    /**
     * Get a internationalized string for the given key
     *
     * @param key  the key to get a associated string for.
     * @param args a list of object, which should be integrated in the string. Which and how many arguments are needed
     * is specified by the string itself
     * @return a internationalized string
     */
    fun getString(key: String, vararg args: Any?): String {
        return try {
            MessageFormat.format(resourceBundle.getString(key), *args)
        } catch (e: MissingResourceException) {
            "[$key]"
        }
    }

    @JvmOverloads
    fun loadBundle(bundleName: String, relativePath: String, locale: Locale = Locale.getDefault()): ResourceBundle {
        return try {
            ResourceBundle.getBundle("$relativePath.$bundleName", locale)
        } catch (e: Exception) {
            LoggerFactory.getLogger(Lang::class.java).error("\nCouldn't find language-files on path '$relativePath'")
            object : ListResourceBundle() {
                override fun getContents(): Array<Array<Any>> {
                    return arrayOf()
                }
            }
        }
    }
}
