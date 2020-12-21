package eu.yeger.gramofo.fol.formula

import java.lang.StringBuilder
import java.util.Arrays
import java.util.HashMap
import java.util.LinkedHashSet

class FOLPredicate : FOLFormula {
    var isInfixProperty = false
        get() = field
        set

    internal constructor(
        children: LinkedHashSet<out FOLFormula?>?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String?
    ) : super(FOLType.Predicate, children, hasBrackets, hasDot, name) {
        isInfixProperty = false
    }

    internal constructor(
        leftOperand: FOLFormula?,
        rightOperand: FOLFormula?,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String?
    ) : super(
        FOLType.Predicate,
        LinkedHashSet<FOLFormula>(Arrays.asList(leftOperand, rightOperand)),
        hasBrackets,
        hasDot,
        name
    ) {
        isInfixProperty = true
    }

    val isEquality: Boolean
        get() = INFIX_EQUALITY == name

    override fun getFormulaString(): String {
        val sb = StringBuilder()
        sb.append(specialNames.getOrDefault(name, name))
        sb.append("(")
        if (children.size > 0) {
            sb.append(getChildAt(0))
            children.stream().skip(1).forEach { child: FOLFormula? -> sb.append(", ").append(child) }
        }
        sb.append(")")
        return sb.toString()
    }

    override fun getFormulaStringForDepth(currentDepth: Int, maxDepth: Int): String {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT
        }
        val sb = StringBuilder()
        if (isInfixProperty) {
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth))
            sb.append(specialNames.getOrDefault(name, name))
            sb.append(getChildAt(1).getFormulaStringForDepth(currentDepth + 1, maxDepth))
        } else {
            sb.append(specialNames.getOrDefault(name, name))
            sb.append("(")
            if (children.size > 0) {
                sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth))
                children.stream().skip(1).forEach { child: FOLFormula ->
                    sb.append(", ").append(child.getFormulaStringForDepth(currentDepth + 1, maxDepth))
                }
            }
            sb.append(")")
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

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
        val specialNames = HashMap<String, String>()
    }

    init {
        specialNames["="] = "\u2250"
        specialNames["<="] = "\u2264"
        specialNames[">="] = "\u2265"
    }
}
