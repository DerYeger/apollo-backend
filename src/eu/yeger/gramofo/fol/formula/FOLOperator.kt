package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

class FOLOperator : FOLFormula {
    constructor(
        type: FOLType,
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula
    ) : super(type, name, setOf(leftOperand, rightOperand))

    constructor(
        type: FOLType,
        name: String,
        operand: FOLFormula,
    ) : super(type, name, setOf(operand))

    override fun getFormulaString(variableBindings: Map<String, Node>): String {
        val sb = StringBuilder()
        if (name == NOT) {
            sb.append(name)
            sb.append(getChildAt(0).getFormulaString(variableBindings))
        } else {
            sb.append(getChildAt(0).getFormulaString(variableBindings))
            sb.append(" ")
            sb.append(name)
            sb.append(" ")
            sb.append(getChildAt(1).getFormulaString(variableBindings))
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
