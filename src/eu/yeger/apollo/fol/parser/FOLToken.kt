package eu.yeger.apollo.fol.parser

/**
 * Token used for parsing formulas.
 *
 * This is legacy code.
 *
 * @property type The current type of the token.
 * @property value The current value of the token.
 * @constructor Creates an [FOLToken] with the given [type] and [value].
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
