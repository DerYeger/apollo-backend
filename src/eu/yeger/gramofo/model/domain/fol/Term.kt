package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node

/**
 * Represents an FOL term ([BoundVariable] or [Function]).
 *
 * @constructor Creates a [Term] with the given name.
 *
 * @param name The name of this term.
 *
 * @author Jan Müller
 */
public abstract class Term(name: String) : FOLEntity(name) {

    /**
     * Evaluates this term to a [Node].
     *
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [Graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @return The [Node] this [Term] evaluates to with the given [symbolTable] and [variableAssignments].
     */
    public abstract fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node
}
