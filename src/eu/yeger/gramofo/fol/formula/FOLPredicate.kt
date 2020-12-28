package eu.yeger.gramofo.fol.formula

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

    override val formulaString: String
        get() {
            val sb = StringBuilder()
            if (isInfix) {
                sb.append(getChildAt(0).formulaString)
                sb.append(specialNames.getOrDefault(name, name))
                sb.append(getChildAt(1).formulaString)
            } else {
                sb.append(specialNames.getOrDefault(name, name))
                sb.append("(")
                if (children.isNotEmpty()) {
                    sb.append(getChildAt(0).formulaString)
                    children.drop(1).forEach { child: FOLFormula -> sb.append(", ").append(child.formulaString) }
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
