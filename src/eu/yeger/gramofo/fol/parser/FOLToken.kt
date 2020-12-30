package eu.yeger.gramofo.fol.parser

class FOLToken(var type: Int, var value: String) {
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
        const val INFIX_FUNC = 14
        const val BRACKET = 15
        const val DOT = 16
        const val COMMA = 17
    }
}
