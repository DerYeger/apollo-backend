package eu.yeger.gramofo.fol.formula

import java.util.*

/**
 * This class is used to store meta data for a FOLFormula.
 */
class FOLFormulaHead(formula: FOLFormula, symbolTable: Map<String, String>?) {
    var formula: FOLFormula
    var symbolTable: Map<String, String>

    init {
        formula.countVariables()
        this.formula = formula
        this.symbolTable = HashMap(symbolTable)
    }
}
