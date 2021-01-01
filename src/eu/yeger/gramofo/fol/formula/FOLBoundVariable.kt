package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Node

class FOLBoundVariable(name: String) : Term(name) {

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return variableAssignments[name]?.name ?: name
    }

    override fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
        return variableAssignments[name]!!
    }
}
