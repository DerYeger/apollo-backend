package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.model.domain.Node

sealed class FOLOperator : FOLFormula {
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

    class Not(operand: FOLFormula) : FOLOperator(FOLType.Not, NOT, operand)
    class And(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(FOLType.And, AND, leftOperand, rightOperand)
    class Or(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(FOLType.Or, OR, leftOperand, rightOperand)
    class Implication(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(FOLType.Implication, IMPLICATION, leftOperand, rightOperand)
    class BiImplication(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(FOLType.BiImplication, BI_IMPLICATION, leftOperand, rightOperand)

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        val sb = StringBuilder()
        if (name == NOT) {
            sb.append(name)
            sb.append(getChildAt(0).getFormulaString(variableAssignments))
        } else {
            sb.append(getChildAt(0).getFormulaString(variableAssignments))
            sb.append(" ")
            sb.append(name)
            sb.append(" ")
            sb.append(getChildAt(1).getFormulaString(variableAssignments))
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
