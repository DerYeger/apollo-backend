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

    override fun toString(): String {
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0))
            sb.append(name)
            sb.append(getChildAt(1))
        } else {
            sb.append(name)
            if (children.isNotEmpty()) {
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
