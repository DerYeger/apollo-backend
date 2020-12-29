package eu.yeger.gramofo.fol.formula

class FOLQuantifier(
    type: FOLType,
    name: String,
    variable: FOLBoundVariable,
    operand: FOLFormula,
) : FOLFormula(type, name, setOf(variable, operand)) {

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

    private fun isUnary(formula: FOLFormula): Boolean {
        val type = formula.type
        return listOf(FOLType.Exists, FOLType.ForAll, FOLType.Not).any { forbiddenType: FOLType -> forbiddenType === type }
    }
}
