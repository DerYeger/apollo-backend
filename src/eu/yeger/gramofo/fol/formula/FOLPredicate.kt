package eu.yeger.gramofo.fol.formula

class FOLPredicate(
    name: String,
    children: Set<FOLFormula>,
) : FOLFormula(
    type = FOLType.Predicate,
    name = name,
    children = children
) {
    private val isInfixProperty = children.size == 2

    constructor(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ) : this(
        name = name,
        children = setOf(leftOperand, rightOperand)
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
            if (children.isNotEmpty()) {
                sb.append(getChildAt(0))
                children.drop(1).forEach { child: FOLFormula? -> sb.append(", ").append(child) }
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
