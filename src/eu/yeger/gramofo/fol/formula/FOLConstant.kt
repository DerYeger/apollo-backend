package eu.yeger.gramofo.fol.formula

class FOLConstant(name: String) : FOLFormula(
    type = FOLType.Constant,
    hasBrackets = false,
    hasDot = false,
    name = name
)
