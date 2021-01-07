package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Node

sealed class Function(name: String) : Term(name) {

    class Constant(name: String) : Function(name) {
        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return name
        }

        override fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            return symbolTable.unarySymbols[name]!!.first()
        }
    }

    class Unary(name: String, val operand: Term) : Function(name) {
        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return "$name(${operand.toString(variableAssignments, true)})"
        }

        override fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            val childResult = operand.evaluate(symbolTable, variableAssignments)
            return symbolTable.binarySymbols[name]!!.first { edge: Edge -> edge.source == childResult }.target
        }
    }
}
