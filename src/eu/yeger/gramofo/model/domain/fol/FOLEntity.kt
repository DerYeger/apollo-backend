package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Node

abstract class FOLEntity(val name: String) {

    var hasBrackets: Boolean = false
    var hasDot: Boolean = false

    abstract fun getFormulaString(variableAssignments: Map<String, Node>): String

    final override fun toString() = toString(emptyMap(), false)

    fun toString(variableAssignments: Map<String, Node>, wrap: Boolean): String {
        val formulaString = getFormulaString(variableAssignments)
        return when (wrap) {
            true -> formulaString.wrapBracketsAndDot()
            false -> formulaString
        }
    }

    private fun String.wrapBracketsAndDot(): String {
        return when (hasBrackets) {
            true -> "($this)"
            false -> this
        }.let {
            when (hasDot) {
                true -> ". $it"
                false -> it
            }
        }
    }
}
