package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.model.domain.Node

class FOLFunction(
    name: String,
    children: Set<FOLFormula>
) : FOLFormula(
    type = FOLType.Function,
    name = name,
    children = children
) {
    private val isInfix = children.size == 2

    constructor(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula
    ) : this(
        name,
        setOf(leftOperand, rightOperand)
    )

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0).getFormulaString(variableAssignments))
            sb.append(name)
            sb.append(getChildAt(1).getFormulaString(variableAssignments))
        } else {
            sb.append(name)
            if (children.isNotEmpty()) {
                sb.append("(")
                sb.append(getChildAt(0).getFormulaString(variableAssignments))
                children.drop(1).forEach { child: FOLFormula -> sb.append(", ").append(child.getFormulaString(variableAssignments)) }
                sb.append(")")
            }
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    companion object {
        fun prefixFunction(name: String, children: Set<FOLFormula>): FOLFunction {
            return FOLFunction(name = name, children = children)
        }

        fun infixFunction(name: String, leftOperand: FOLFormula, rightOperand: FOLFormula): FOLFunction {
            return FOLFunction(name = name, leftOperand = leftOperand, rightOperand = rightOperand)
        }
    }
}
