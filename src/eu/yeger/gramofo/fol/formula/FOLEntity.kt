package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.model.domain.Node

interface FOLEntity {

    val name: String
    var hasBrackets: Boolean
    var hasDot: Boolean

    fun getFormulaString(variableAssignments: Map<String, Node>): String

    fun toString(variableAssignments: Map<String, Node>): String {
        return getFormulaString(variableAssignments).removePrefix(". ")
    }

    fun StringBuilder.maybeWrapBracketsAndDot() = apply {
        insert(0, if (hasBrackets) "(" else "")
        insert(0, if (hasDot) ". " else "")
        append(if (hasBrackets) ")" else "")
    }
}
