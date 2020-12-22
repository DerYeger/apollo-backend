package eu.yeger.gramofo.fol.formula

class FOLConstant(name: String) : FOLFormula(FOLType.Constant, false, false, name) {
    override fun toString(): String {
        return name
    }
}
