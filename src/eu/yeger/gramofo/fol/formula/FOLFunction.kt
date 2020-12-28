package eu.yeger.gramofo.fol.formula

class FOLFunction(
    name: String,
    children: Set<FOLFormula>
) : FOLFormula(
    type = FOLType.Function,
    name = name,
    children = children
) {
    private val isInfix = children.size == 2

    constructor(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula
    ) : this(
        name,
        setOf(leftOperand, rightOperand)
    )

    override val formulaString: String
        get() {
            val sb = StringBuilder()
            if (isInfix) {
                sb.append(getChildAt(0).formulaString)
                sb.append(name)
                sb.append(getChildAt(1).formulaString)
            } else {
                sb.append(name)
                if (children.isNotEmpty()) {
                    sb.append("(")
                    sb.append(getChildAt(0).formulaString)
                    children.drop(1).forEach { child: FOLFormula -> sb.append(", ").append(child.formulaString) }
                    sb.append(")")
                }
            }
            maybeWrapBracketsAndDot(sb)
            return sb.toString()
        }
}
