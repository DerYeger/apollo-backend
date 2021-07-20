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

/**
 * Backing [Map] that associates a setting with its values.
 *
 * @author Arno Ehle
 * @author Benedikt Hruschka
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
 * @author Arno Ehle
 * @author Benedikt Hruschka
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
