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
 * Token used for parsing formulas.
 *
 * @property type The current type of the token.
 * @property value The current value of the token.
 * @constructor Creates an [FOLToken] with the given [type] and [value].
 *
 * @author Arno Ehle
 * @author Benedikt Hruschka
 */
internal class FOLToken(var type: Int, var value: String) {

  /**
   * Sets [type] and [value] of an [FOLToken].
   *
   * @param type The new type of the token.
   * @param value The new value of the token.
   */
  fun setTypeAndValue(type: Int, value: String) {
    this.type = type
    this.value = value
  }

  companion object {
    const val END_OF_SOURCE = 0
    const val SYMBOL = 1
    const val CHAR = 2
    const val TRUE = 3
    const val FALSE = 4
    const val OR = 5
    const val AND = 6
    const val NOT = 7
    const val IMPLICATION = 8
    const val BI_IMPLICATION = 9
    const val EXISTS = 10
    const val FOR_ALL = 11
    const val INFIX_PRED = 12
    const val EQUAL_SIGN = 13
    const val BRACKET = 14
    const val DOT = 15
    const val COMMA = 16
  }
}
