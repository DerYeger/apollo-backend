package eu.yeger.gramofo.fol.formula

import java.lang.StringBuilder
import java.util.LinkedHashSet

class FOLPredicate(
    children: LinkedHashSet<out FOLFormula>,
    hasBrackets: Boolean,
    hasDot: Boolean,
    name: String
) : FOLFormula(
    type = FOLType.Predicate,
    hasBrackets = hasBrackets,
    hasDot = hasDot,
    name = name,
    children = children
) {
    private val isInfixProperty = children.size == 2

    constructor(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ) : this(
        children = LinkedHashSet<FOLFormula>(listOf(leftOperand, rightOperand)),
        hasBrackets = hasBrackets,
        hasDot = hasDot,
        name = name,
    )

    override fun toString(): String {
        val sb = StringBuilder()
        if (isInfixProperty) {
            sb.append(getChildAt(0))
            sb.append(specialNames.getOrDefault(name, name))
            sb.append(getChildAt(1))
        } else {
            sb.append(specialNames.getOrDefault(name, name))
            sb.append("(")
            if (children.size > 0) {
                sb.append(getChildAt(0))
                children.stream().skip(1).forEach { child: FOLFormula? -> sb.append(", ").append(child) }
            }
            sb.append(")")
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    companion object {
        val specialNames = mapOf(
            "=" to "\u2250",
            "<=" to "\u2264",
            ">=" to "\u2265"
        )
    }
}
