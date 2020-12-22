package eu.yeger.gramofo.fol.formula

class FOLOperator : FOLFormula {
    constructor(
        type: FOLType,
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula
    ) : super(type, name, setOf(leftOperand, rightOperand))

    constructor(
        type: FOLType,
        name: String,
        operand: FOLFormula,
    ) : super(type, name, setOf(operand))

    override fun toString(): String {
        val sb = StringBuilder()
        if (name == NOT) {
            sb.append(name)
            sb.append(getChildAt(0))
        } else {
            sb.append(getChildAt(0))
            sb.append(" ")
            sb.append(name)
            sb.append(" ")
            sb.append(getChildAt(1))
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
