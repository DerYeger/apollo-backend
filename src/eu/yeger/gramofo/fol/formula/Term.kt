package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerException
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Node

interface Term : FOLEntity {

    @Throws(ModelCheckerException::class)
    fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node
}
