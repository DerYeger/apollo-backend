package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Vertex

class FOLBoundVariable(name: String) : FOLFormula(
    type = FOLType.Variable,
    name = name
) {
    override fun getFormulaString(variableBindings: Map<String, Vertex>): String {
        return variableBindings[name]?.name ?: name
    }
}
