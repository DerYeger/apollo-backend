package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.model.domain.Node

sealed class FOLConstant(name: String) : FOLFormula(
    type = FOLType.Constant,
    name = name
) {
    class True : FOLConstant(TT)
    class False : FOLConstant(FF)

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return name
    }
}
