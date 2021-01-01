package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Node

abstract class Term(name: String) : FOLEntity(name) {
    abstract fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node
}
