package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node

/**
 * Represents a [Term] function.
 *
 * @constructor Creates a [Function] with the given name.
 *
 * @param name The name of this function.
 *
 * @author Jan Müller
 */
public sealed class Function(name: String) : Term(name) {

    /**
     * Represents a [Term] constant.
     *
     * @constructor Creates a [Constant] with the given name.
     *
     * @param name The name of this constant.
     *
     * @author Jan Müller
     */
    public class Constant(name: String) : Function(name) {
        /**
         * Returns the [name] of this constant.
         *
         * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation. Unused.
         * @return The [name] of this constant.
         */
        override fun getRawString(variableAssignments: Map<String, Node>): String {
            return name
        }

        /**
         * Evaluates this term to a [Node] using the given [symbolTable].
         *
         * @throws [NullPointerException] if no node is assigned.
         * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [Graph].
         * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s. Unused.
         * @return The [Node] this [Term] evaluates to with the given [symbolTable].
         */
        override fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            return symbolTable.unarySymbols[name]!!.first()
        }
    }

    /**
     * Represents an unary [Term].
     *
     * @constructor Creates an [Unary] term with the given name.
     *
     * @param name The name of this unary term.
     * @property operand The operand of this unary term.
     *
     * @author Jan Müller
     */
    public class Unary(name: String, public val operand: Term) : Function(name) {

        /**
         * Returns raw [String] representation of this unary term.
         *
         * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation. Unused.
         * @return The raw [String] representation of this unary term.
         */
        override fun getRawString(variableAssignments: Map<String, Node>): String {
            return "$name(${operand.toString(variableAssignments, true)})"
        }

        /**
         * Evaluates this term to a [Node] using the given [symbolTable].
         *
         * @throws [NullPointerException] if no node is assigned.
         * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [Graph].
         * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s. Unused.
         * @return The [Node] this [Term] evaluates to with the given [symbolTable].
         */
        override fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            val childResult = operand.evaluate(symbolTable, variableAssignments)
            return symbolTable.binarySymbols[name]!!.first { edge: Edge -> edge.source == childResult }.target
        }
    }
}
