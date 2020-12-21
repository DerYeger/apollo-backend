package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLOperator : FOLFormula {
    internal constructor(
        type: FOLType?,
        leftOperand: FOLFormula?,
        rightOperand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String?
    ) : super(type, LinkedHashSet<FOLFormula>(listOf(leftOperand, rightOperand)), hasBrackets, hasDot, name)

    internal constructor(
        type: FOLType?,
        operand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String?
    ) : super(type, LinkedHashSet<FOLFormula>(listOf(operand)), hasBrackets, hasDot, name)

    override fun getFormulaStringForDepth(currentDepth: Int, maxDepth: Int): String {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT
        }
        val sb = StringBuilder()
        if (name == NOT) {
            sb.append(name)
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth))
        } else {
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth))
            sb.append(" ")
            sb.append(name)
            sb.append(" ")
            sb.append(getChildAt(1).getFormulaStringForDepth(currentDepth + 1, maxDepth))
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

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
