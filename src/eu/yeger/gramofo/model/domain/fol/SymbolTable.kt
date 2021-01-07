package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Node

data class SymbolTable(
    val unarySymbols: Map<String, Set<Node>>,
    val binarySymbols: Map<String, Set<Edge>>,
    val symbolTypes: Map<String, String>,
)
