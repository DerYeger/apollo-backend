package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

class FOLConstant(name: String) : FOLFormula(
    type = FOLType.Constant,
    name = name
) {
    override fun getFormulaString(variableBindings: Map<String, Node>): String {
        return name
    }
}
