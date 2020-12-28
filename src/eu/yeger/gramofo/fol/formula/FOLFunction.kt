package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

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

    override fun getFormulaString(variableBindings: Map<String, Node>): String {
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0).getFormulaString(variableBindings))
            sb.append(name)
            sb.append(getChildAt(1).getFormulaString(variableBindings))
        } else {
            sb.append(name)
            if (children.isNotEmpty()) {
                sb.append("(")
                sb.append(getChildAt(0).getFormulaString(variableBindings))
                children.drop(1).forEach { child: FOLFormula -> sb.append(", ").append(child.getFormulaString(variableBindings)) }
                sb.append(")")
            }
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
