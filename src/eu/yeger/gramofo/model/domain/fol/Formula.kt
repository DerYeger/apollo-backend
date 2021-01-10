package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node

abstract class Formula(name: String) : FOLEntity(name) {

    abstract fun fullCheck(graph: Graph, symbolTable: SymbolTable, variableAssignments: Map<String, Node>, shouldBeModel: Boolean): ModelCheckerTrace

    abstract fun partialCheck(graph: Graph, symbolTable: SymbolTable, variableAssignments: Map<String, Node>, shouldBeModel: Boolean): ModelCheckerTrace

    companion object {
        const val TT = "tt"
        const val FF = "ff"
        const val NOT = "\u00AC"
        const val AND = "\u2227"
        const val OR = "\u2228"
        const val IMPLICATION = "\u2192"
        const val BI_IMPLICATION = "\u2194"
        const val EXISTS = "\u2203"
        const val FOR_ALL = "\u2200"
        const val INFIX_EQUALITY = "=" // equal sign with a dot on top
    }
}
