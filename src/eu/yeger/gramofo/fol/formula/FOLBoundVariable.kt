package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

class FOLBoundVariable(name: String) : FOLFormula(
    type = FOLType.Variable,
    name = name
) {
    override fun getFormulaString(variableBindings: Map<String, Node>): String {
        return variableBindings[name]?.name ?: name
    }
}
