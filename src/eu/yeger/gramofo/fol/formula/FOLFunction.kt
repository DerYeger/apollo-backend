package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLFunction : FOLFormula {
    var isInfix: Boolean

    internal constructor(
        children: LinkedHashSet<out FOLFormula?>?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String?
    ) : super(FOLType.Function, children, hasBrackets, hasDot, name) {
        isInfix = false
    }

    internal constructor(
        leftOperand: FOLFormula?,
        rightOperand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String?
    ) : super(
        FOLType.Function,
        LinkedHashSet<FOLFormula>(Arrays.asList(leftOperand, rightOperand)),
        hasBrackets,
        hasDot,
        name
    ) {
        isInfix = true
    }

    override fun getFormulaStringForDepth(currentDepth: Int, maxDepth: Int): String {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT
        }
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth))
            sb.append(name)
            sb.append(getChildAt(1).getFormulaStringForDepth(currentDepth + 1, maxDepth))
        } else {
            sb.append(name)
            if (children.size > 0) {
                sb.append("(")
                sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth))
                children.stream().skip(1).forEach { child: FOLFormula ->
                    sb.append(", ").append(child.getFormulaStringForDepth(currentDepth + 1, maxDepth))
                }
                sb.append(")")
            }
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0))
            sb.append(name)
            sb.append(getChildAt(1))
        } else {
            sb.append(name)
            if (children.size > 0) {
                sb.append("(")
                sb.append(getChildAt(0))
                children.stream().skip(1).forEach { child: FOLFormula? -> sb.append(", ").append(child) }
                sb.append(")")
            }
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
