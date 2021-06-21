package eu.yeger.apollo.model.domain.fol

/**
 * This class is used to store meta data for a FOLFormula.
 *
 * This is legacy code.
 *
 * @property formula The root [Formula].
 * @property symbolTable The extracted, raw symbol table.
 * @constructor Creates a [FormulaHead] with the given parameters.
 */
public data class FormulaHead(
  val formula: Formula,
  val symbolTable: Map<String, String>
)
