package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Node

sealed class Function(name: String) : Term(name) {

    class Constant(name: String) : Function(name) {
        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return buildString {
                append(name)
                maybeWrapBracketsAndDot()
            }
        }

        override fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            return symbolTable.unarySymbols[name]!!.first()
        }
    }

    class Unary(name: String, val operand: Term) : Function(name) {
        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return buildString {
                append(name)
                append("(")
                append(operand.getFormulaString(variableAssignments))
                append(")")
                maybeWrapBracketsAndDot()
            }
        }

        override fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            val childResult = operand.interpret(symbolTable, variableAssignments)
            return symbolTable.binarySymbols[name]!!.first { edge: Edge -> edge.source == childResult }.target
        }
    }
}
