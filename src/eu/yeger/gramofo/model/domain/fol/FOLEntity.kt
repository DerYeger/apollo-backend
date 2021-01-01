package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Node

abstract class FOLEntity(val name: String) {

    var hasBrackets: Boolean = false
    var hasDot: Boolean = false

    abstract fun getFormulaString(variableAssignments: Map<String, Node>): String

    fun toString(variableAssignments: Map<String, Node>): String {
        return getFormulaString(variableAssignments).removePrefix(". ")
    }

    fun StringBuilder.maybeWrapBracketsAndDot() = apply {
        insert(0, if (hasBrackets) "(" else "")
        insert(0, if (hasDot) ". " else "")
        append(if (hasBrackets) ")" else "")
    }
}
