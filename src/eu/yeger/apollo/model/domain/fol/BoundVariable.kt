package eu.yeger.apollo.model.domain.fol

import eu.yeger.apollo.model.domain.Graph
import eu.yeger.apollo.model.domain.Node

/**
 * Represents an FOL variable bound by a [Quantifier].
 *
 * @constructor Creates a [BoundVariable] with the given name.
 *
 * @param name The name of this bound variable.
 *
 * @author Jan Müller
 */
public class BoundVariable(name: String) : Term(name) {

  /**
   * Returns the name of the assigned [Node] or the [name] of this [BoundVariable] if none is assigned.
   *
   * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
   * @return The [String] representation.
   */
  override fun getRawString(variableAssignments: Map<String, Node>): String {
    return variableAssignments[name]?.name ?: name
  }

  /**
   * Evaluates this term to a [Node] using the given [variableAssignments].
   *
   * @throws [NullPointerException] if no node is assigned.
   * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [Graph]. Unused.
   * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
   * @return The [Node] this [Term] evaluates to with the given [variableAssignments].
   */
  override fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
    return variableAssignments[name]!!
  }
}
