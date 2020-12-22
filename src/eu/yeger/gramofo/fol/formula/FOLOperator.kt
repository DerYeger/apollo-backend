package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLOperator : FOLFormula {
    constructor(
        type: FOLType?,
        leftOperand: FOLFormula?,
        rightOperand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ) : super(type, hasBrackets, hasDot, name, LinkedHashSet<FOLFormula>(listOf(leftOperand, rightOperand)))

    constructor(
        type: FOLType?,
        operand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ) : super(type, hasBrackets, hasDot, name, LinkedHashSet<FOLFormula>(listOf(operand)))

    override fun toString(): String {
        val sb = StringBuilder()
        if (name == NOT) {
            sb.append(name)
            sb.append(getChildAt(0))
        } else {
            sb.append(getChildAt(0))
            sb.append(" ")
            sb.append(name)
            sb.append(" ")
            sb.append(getChildAt(1))
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
