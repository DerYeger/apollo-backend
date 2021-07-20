/**
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Arno Ehle, Benedikt Hruschka
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.yeger.apollo.fol.parser

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
 * @constructor Creates a [Language] using the given [Locale]. This will non-lazy load the translations. If the translation file does not exist, all translations attempts will return the translation key.
 * @param locale The [Locale] of the language.
 *
 * @author Arno Ehle
 * @author Benedikt Hruschka
 */
public sealed class Language(locale: Locale) {
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
  public fun getString(key: String): String {
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
  public fun getString(key: String, vararg args: Any?): String {
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
public object English : Language(Locale.ENGLISH)

/**
 * German [Language] object.
 *
 * @author Jan Müller
 */
public object German : Language(Locale.GERMAN)
