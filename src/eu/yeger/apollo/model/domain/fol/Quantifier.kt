package eu.yeger.apollo.model.domain.fol

import eu.yeger.apollo.fol.invalidated
import eu.yeger.apollo.fol.validated
import eu.yeger.apollo.model.domain.Graph
import eu.yeger.apollo.model.domain.Node
import eu.yeger.apollo.model.dto.TranslationDTO

/**
 * Represents an FOL quantifier.
 *
 * @property variable The [BoundVariable] of this quantifier.
 * @property operand The operand of this quantifier.
 * @constructor Creates a [Quantifier] with the given name, variable and operand.
 *
 * @param name The name of this quantifier.
 *
 * @author Jan Müller
 */
public sealed class Quantifier(
  name: String,
  public val variable: BoundVariable,
  public val operand: Formula,
) : Formula(name) {

  /**
   * Represents an existential FOL quantifier.
   *
   * @constructor Creates an [Existential] quantifier with the given variable and operand.
   *
   * @param variable The [BoundVariable] of this existential quantifier.
   * @param operand The operand of this existential quantifier.
   *
   * @author Jan Müller
   */
  public class Existential(variable: BoundVariable, operand: Formula) : Quantifier("\u2203", variable, operand) {

    /**
     * Checks all possible variable assignments.
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun fullCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      val children = graph.nodes.map { node: Node ->
        operand.fullCheck(graph, symbolTable, variableAssignments + (variable.name to node), shouldBeModel)
      }
      return if (children.any(ModelCheckerTrace::isModel)) {
        validated(TranslationDTO("api.exists.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
      } else {
        invalidated(TranslationDTO("api.exists.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
      }
    }

    /**
     * Checks all possible variable assignments, until one is positive.
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun partialCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      val children = graph.nodes.map { node: Node ->
        val childTrace = operand.partialCheck(graph, symbolTable, variableAssignments + (variable.name to node), shouldBeModel)
        if (childTrace.isModel) {
          return@partialCheck validated(TranslationDTO("api.exists.valid"), variableAssignments, shouldBeModel, childTrace)
        }
        childTrace
      }
      return invalidated(TranslationDTO("api.exists.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
    }
  }

  /**
   * Represents a universal FOL quantifier.
   *
   * @constructor Creates an [Universal] quantifier with the given variable and operand.
   *
   * @param variable The [BoundVariable] of this universal quantifier.
   * @param operand The operand of this universal quantifier.
   *
   * @author Jan Müller
   */
  public class Universal(variable: BoundVariable, operand: Formula) : Quantifier("\u2200", variable, operand) {

    /**
     * Checks all possible variable assignments.
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun fullCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      val children = graph.nodes.map { node: Node ->
        operand.fullCheck(graph, symbolTable, variableAssignments + (variable.name to node), shouldBeModel)
      }
      return if (children.all(ModelCheckerTrace::isModel)) {
        validated(TranslationDTO("api.forall.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
      } else {
        invalidated(TranslationDTO("api.forall.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
      }
    }

    /**
     * Checks all possible variable assignments, until one is negative.
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun partialCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      val children = graph.nodes.map { node: Node ->
        val childTrace = operand.partialCheck(graph, symbolTable, variableAssignments + (variable.name to node), shouldBeModel)
        if (childTrace.isModel.not()) {
          return@partialCheck invalidated(TranslationDTO("api.forall.invalid"), variableAssignments, shouldBeModel, childTrace)
        }
        childTrace
      }
      return validated(TranslationDTO("api.forall.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
    }
  }

  /**
   * Returns raw [String] representation of this quantifier.
   *
   * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
   * @return The raw [String] representation of this quantifier.
   */
  override fun getRawString(variableAssignments: Map<String, Node>): String {
    val variableString = variable.toString(variableAssignments, true)
    val operandString = operand.toString(variableAssignments, true)
    val separator = when (!operand.hasDot && !isUnary(operand)) {
      true -> " "
      false -> ""
    }
    return "$name$variableString$separator$operandString"
  }

  /**
   * Checks if a [Formula] is unary.
   *
   * @param formula The [Formula] that will be checked.
   * @return True if [formula] is unary and false otherwise.
   */
  private fun isUnary(formula: Formula): Boolean {
    return when (formula) {
      is Existential -> true
      is Universal -> true
      is Operator.Unary.Negation -> true
      else -> false
    }
  }
}
