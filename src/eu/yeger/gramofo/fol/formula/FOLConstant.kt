package eu.yeger.gramofo.fol.formula

class FOLConstant internal constructor(name: String?) : FOLFormula(FOLType.Constant, null, false, false, name) {
    override fun getFormulaStringForDepth(currentDepth: Int, maxDepth: Int): String {
        return if (currentDepth >= maxDepth && maxDepth != -1) {
            DOT
        } else name
    }

    override fun toString(): String {
        return name
    }
}
