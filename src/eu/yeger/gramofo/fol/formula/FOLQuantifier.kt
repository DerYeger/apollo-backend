package eu.yeger.gramofo.fol.formula

import java.util.*

class FOLQuantifier internal constructor(
    type: FOLType?,
    variable: FOLBoundVariable?,
    operand: FOLFormula?,
    hasBrackets: Boolean,
    hasDot: Boolean,
    name: String?
) : FOLFormula(type, LinkedHashSet(listOf(variable, operand)), hasBrackets, hasDot, name) {

    override fun getFormulaString(): String {
        val sb = StringBuilder()
        sb.append(name)
        sb.append(getChildAt(0))
        return sb.toString()
    }

    override fun getFormulaStringForDepth(currentDepth: Int, maxDepth: Int): String {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT
        }
        val child0 = getChildAt(0)
        val child1 = getChildAt(1)
        val sb = StringBuilder()
        sb.append(name)
        sb.append(child0)
        if (!child1.hasDot && !isUnary(child1)) {
            sb.append(" ")
        }
        sb.append(child1.getFormulaStringForDepth(currentDepth, maxDepth))
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    override fun toString(): String {
        val child0 = getChildAt(0)
        val child1 = getChildAt(1)
        val sb = StringBuilder()
        sb.append(name)
        sb.append(child0)
        if (!child1.hasDot && !isUnary(child1)) {
            sb.append(" ")
        }
        sb.append(child1)
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    fun isUnary(formula: FOLFormula): Boolean {
        val type = formula.type
        return Arrays.asList(FOLType.Exists, FOLType.ForAll, FOLType.Not).stream()
            .anyMatch { forbiddenType: FOLType -> forbiddenType === type }
    }
}
