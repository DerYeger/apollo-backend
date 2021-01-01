package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Node

sealed class FOLFunction(name: String) : Term(name) {

    class Constant(name: String) : FOLFunction(name) {
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

    class Unary(name: String, val operand: Term) : FOLFunction(name) {
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

    class Infix(name: String, val leftOperand: Term, val rightOperand: Term) : FOLFunction(name) {
        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            TODO("Not yet implemented")
        }

        override fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
            TODO("Not yet implemented")
        }
    }
}
