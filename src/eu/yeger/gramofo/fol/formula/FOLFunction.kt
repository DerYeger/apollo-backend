package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLFunction : FOLFormula {
    var isInfix: Boolean

    constructor(
        children: LinkedHashSet<out FOLFormula?>?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ) : super(FOLType.Function, hasBrackets, hasDot, name, children) {
        isInfix = false
    }

    constructor(
        leftOperand: FOLFormula?,
        rightOperand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ) : super(
        FOLType.Function,
        hasBrackets,
        hasDot,
        name,
        LinkedHashSet<FOLFormula>(listOf(leftOperand, rightOperand)),
    ) {
        isInfix = true
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
