package eu.yeger.gramofo.model.domain.fol

/**
 * This class is used to store meta data for a FOLFormula.
 */
class FormulaHead(
    val formula: Formula,
    val symbolTable: Map<String, String>
)
