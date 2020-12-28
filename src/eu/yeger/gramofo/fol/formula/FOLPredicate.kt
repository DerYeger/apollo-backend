package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

private val specialNames = mapOf(
    "=" to "\u2250",
    "<=" to "\u2264",
    ">=" to "\u2265"
)

class FOLPredicate(
    name: String,
    children: Set<FOLFormula>,
    private val isInfix: Boolean
) : FOLFormula(
    type = FOLType.Predicate,
    name = name,
    children = children
) {

    constructor(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ) : this(
        name = name,
        children = setOf(leftOperand, rightOperand),
        isInfix = true
    )

    override fun getFormulaString(variableBindings: Map<String, Node>): String {
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0).getFormulaString(variableBindings))
            sb.append(specialNames.getOrDefault(name, name))
            sb.append(getChildAt(1).getFormulaString(variableBindings))
        } else {
            sb.append(specialNames.getOrDefault(name, name))
            sb.append("(")
            if (children.isNotEmpty()) {
                sb.append(getChildAt(0).getFormulaString(variableBindings))
                children.drop(1).forEach { child: FOLFormula -> sb.append(", ").append(child.getFormulaString(variableBindings)) }
            }
            sb.append(")")
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
