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

    override val formulaString: String
        get() {
            val sb = StringBuilder()
            if (name == NOT) {
                sb.append(name)
                sb.append(getChildAt(0).formulaString)
            } else {
                sb.append(getChildAt(0).formulaString)
                sb.append(" ")
                sb.append(name)
                sb.append(" ")
                sb.append(getChildAt(1).formulaString)
            }
            maybeWrapBracketsAndDot(sb)
            return sb.toString()
        }
}
