package eu.yeger.gramofo.fol.parser

import org.slf4j.LoggerFactory
import java.lang.Exception
import java.text.MessageFormat
import java.util.ListResourceBundle
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

/**
 * Represents a language and offers key-bases translations with optional arguments.
 * Because the parser was supposed to remain largely untouched, I (Jan Müller) did not move the parser-translations to the web-frontend, as I did with all other translations.
 *
 * This is legacy code.
 *
 * @constructor Creates a [Language] using the given [Locale]. This will non-lazy load the translations. If the translation file does not exist, all translations attempts will return the translation key.
 * @param locale The [Locale] of the language.
 *

 */
sealed class Language(locale: Locale) {
    private val resourceBundle =
        try {
            ResourceBundle.getBundle("language.lang", locale)
        } catch (e: Exception) {
            LoggerFactory.getLogger(Language::class.java).error("\nCouldn't find language-files on path 'language'")
            object : ListResourceBundle() {
                override fun getContents(): Array<Array<Any>> {
                    return arrayOf()
                }
            }
        }

    /**
     * Fetches the translation for a given key.
     *
     * @param key The key for the translation.
     * @return The translated [String].
     */
    fun getString(key: String): String {
        return try {
            resourceBundle.getString(key)
        } catch (e: MissingResourceException) {
            key
        }
    }

    /**
     * Fetches the translation for a given key and parameters.
     *
     * @param key  The key for the translation.
     * @param args [List] of arguments, which are used for formatting.
     * @return The translated [String].
     */
    fun getString(key: String, vararg args: Any?): String {
        return try {
            MessageFormat.format(resourceBundle.getString(key), *args)
        } catch (e: MissingResourceException) {
            key
        }
    }
}

/**
 * English [Language] object.
 *
 * @author Jan Müller
 */
object English : Language(Locale.ENGLISH)

/**
 * German [Language] object.
 *
 * @author Jan Müller
 */
object German : Language(Locale.GERMAN)
