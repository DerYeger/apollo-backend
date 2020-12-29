package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

class FOLBoundVariable(name: String) : FOLFormula(
    type = FOLType.Variable,
    name = name
) {
    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return variableAssignments[name]?.name ?: name
    }
}
