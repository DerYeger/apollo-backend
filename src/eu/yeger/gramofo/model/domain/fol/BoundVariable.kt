package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Node

class BoundVariable(name: String) : Term(name) {

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return variableAssignments[name]?.name ?: name
    }

    override fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
        return variableAssignments[name]!!
    }
}
