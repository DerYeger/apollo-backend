package eu.yeger.apollo.shared.model.fol

import eu.yeger.apollo.shared.model.domain.Edge
import eu.yeger.apollo.shared.model.domain.Node

/**
 * DTO containing precalculated meta information for the ModelChecking algorithm.
 *
 * This class does not have separate properties for relations and constants/functions for compatibility with legacy code.
 *
 * @property unarySymbols [Map] between unary symbols and their assigned [Node]s. Contains both unary relations and constants.
 * @property binarySymbols [Map] between binary symbols and their assigned [Node]s. Contains both binary relations and functions.
 * @property symbolTypes [Map] associating unary and binary symbols with their respective type.
 * @constructor Creates a [SymbolTable] with the given parameters.
 *
 * @author Jan Müller
 */
public data class SymbolTable(
  val unarySymbols: Map<String, Set<Node>>,
  val binarySymbols: Map<String, Set<Edge>>,
  val symbolTypes: Map<String, String>,
)
