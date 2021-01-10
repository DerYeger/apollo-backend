package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Node

abstract class Term(name: String) : FOLEntity(name) {
    abstract fun evaluate(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node
}
