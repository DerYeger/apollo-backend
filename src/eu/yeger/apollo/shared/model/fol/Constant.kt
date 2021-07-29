package eu.yeger.apollo.shared.model.fol

import eu.yeger.apollo.fol.invalidated
import eu.yeger.apollo.fol.validated
import eu.yeger.apollo.shared.model.api.TranslationDTO
import eu.yeger.apollo.shared.model.domain.Graph
import eu.yeger.apollo.shared.model.domain.Node

/**
 * Represents an FOL constant that is either [True] or [False].
 *
 * @constructor Creates a [Constant] with the given name.
 *
 * @param name The name if this constant.
 *
 * @author Jan Müller
 */
public sealed class Constant(name: String) : Formula(name = name) {

  /**
   * The true-constant.
   *
   * Can NOT be a Kotlin-object for compatibility with the legacy parser.
   *
   * @constructor Creates a [True]-constant.
   *
   * @author Jan Müller
   */
  public class True : Constant("tt")

  /**
   * The false-constant.
   *
   * Can NOT be a Kotlin-object for compatibility with the legacy parser.
   *
   * @constructor Creates a [False]-constant.
   *
   * @author Jan Müller
   */
  public class False : Constant("ff")

  /**
   * Checks if this constant is [True] or [False].
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
    return when (this) {
      is True -> validated(TranslationDTO("api.constant.true"), variableAssignments, shouldBeModel)
      is False -> invalidated(TranslationDTO("api.constant.false"), variableAssignments, shouldBeModel)
    }
  }

  /**
   * Checks if this constant is [True] or [False].
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
    return fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
  }

  /**
   * Returns the [name] of this constant.
   *
   * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation. Unused.
   * @return The [name] of this constant.
   */
  override fun getRawString(variableAssignments: Map<String, Node>): String {
    return name
  }
}
