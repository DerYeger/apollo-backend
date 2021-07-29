package eu.yeger.apollo.shared.model.fol

import eu.yeger.apollo.shared.model.domain.Graph
import eu.yeger.apollo.shared.model.domain.Node

/**
 * Represents an FOL formula and contains methods for ModelChecking.
 *
 * @constructor Creates a [Formula] with the given name.
 *
 * @param name The name this formula.
 *
 * @author Jan Müller
 */
public sealed class Formula(name: String) : FOLEntity(name) {

  /**
   * Recursively evaluate all subformulas, including redundant checks.
   *
   * @param graph The [Graph] that will be checked.
   * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
   * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
   * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
   * @return [ModelCheckerTrace] that contains the results of this check.
   */
  public abstract fun fullCheck(graph: Graph, symbolTable: SymbolTable, variableAssignments: Map<String, Node>, shouldBeModel: Boolean): ModelCheckerTrace

  /**
   * Recursively evaluate some subformulas, excluding redundant checks.
   *
   * @param graph The [Graph] that will be checked.
   * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
   * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
   * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
   * @return [ModelCheckerTrace] that contains the results of this check.
   */
  public abstract fun partialCheck(graph: Graph, symbolTable: SymbolTable, variableAssignments: Map<String, Node>, shouldBeModel: Boolean): ModelCheckerTrace
}
