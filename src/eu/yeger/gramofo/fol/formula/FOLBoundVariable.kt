package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerException
import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node

class FOLBoundVariable(name: String) : FOLFormula(name), Term {
    override fun checkModel(
        graph: Graph,
        symbolTable: SymbolTable,
        variableAssignments: Map<String, Node>,
        shouldBeModel: Boolean,
    ): ModelCheckerTrace {
        throw ModelCheckerException("[ModelChecker][Internal error] checkModel cannot be called for instances of FOLBoundVariable")
    }

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return variableAssignments[name]?.name ?: name
    }

    override fun interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
        return variableAssignments[name]!!
    }
}
