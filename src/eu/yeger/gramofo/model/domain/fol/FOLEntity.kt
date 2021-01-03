package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Node

abstract class FOLEntity(val name: String) {

    var hasBrackets: Boolean = false
    var hasDot: Boolean = false

    abstract fun getFormulaString(variableAssignments: Map<String, Node>): String

    fun toString(variableAssignments: Map<String, Node>): String {
        return getFormulaString(variableAssignments).removePrefix(". ")
    }

    protected fun String.maybeWrapBracketsAndDot(): String {
        return when (hasBrackets) {
            true -> "($this)"
            false -> this
        }.let {
            when (hasDot) {
                true -> ". $this"
                false -> this
            }
        }
    }
}
