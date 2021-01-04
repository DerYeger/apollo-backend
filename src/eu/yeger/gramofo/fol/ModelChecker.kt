package eu.yeger.gramofo.fol

import com.github.michaelbull.result.*
import com.github.michaelbull.result.binding
import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.domain.fol.*
import eu.yeger.gramofo.model.dto.TranslationDTO
import java.util.*

typealias ModelCheckerResult = Result<ModelCheckerTrace, TranslationDTO>

data class ModelCheckerTrace(
    val formula: String,
    val description: TranslationDTO,
    val isModel: Boolean,
    val shouldBeModel: Boolean,
    val children: List<ModelCheckerTrace>,
)

data class SymbolTable(
    val unarySymbols: Map<String, Set<Node>>,
    val binarySymbols: Map<String, Set<Edge>>,
    val symbolTypes: Map<String, String>,
)

fun checkModel(graph: Graph, formulaHead: FormulaHead): ModelCheckerResult = binding {
    val symbolTable = graph.loadSymbols()
        .andThen { symbolTable -> formulaHead.loadSymbols(symbolTable) }
        .andThen { symbolTable -> checkTotality(graph, symbolTable) }.bind()
    runCatching { formulaHead.formula.checkModel(graph, symbolTable, emptyMap(), true) }
        .mapError { error -> TranslationDTO(error.message ?: "api.error.unknown") }
        .bind()
}
/**
 * Iterates over the graph and puts all found symbols in a symbol table.
 */
private fun Graph.loadSymbols(): Result<SymbolTable, TranslationDTO> {
    val unarySymbols = mutableMapOf<String, MutableSet<Node>>()
    val binarySymbols = mutableMapOf<String, MutableSet<Edge>>()
    val symbolTypes = mutableMapOf<String, String>()
    nodes.forEach { node: Node ->
        node.constants.forEach { constant ->
            symbolTypes[constant] = "F-0"
            val relationSet = unarySymbols.getOrPut(constant) { mutableSetOf() }
            if (relationSet.isNotEmpty()) {
                return@loadSymbols Err(TranslationDTO(key = "api.error.duplicate-constant", "constant" to constant))
            }
            relationSet.add(node)
        }
        node.relations.forEach { relation ->
            symbolTypes[relation] = "P-1"
            val relationSet = unarySymbols.getOrPut(relation) { mutableSetOf() }
            relationSet.add(node)
        }
    }
    edges.forEach { edge: Edge ->
        edge.functions.forEach { function ->
            symbolTypes.putIfAbsent(function, "F-1")
            if (symbolTypes[function] != "F-1") {
                return@loadSymbols Err(TranslationDTO("api.error.different-arities", "symbol" to function))
            }
            val relationSet = binarySymbols.getOrPut(function) { mutableSetOf() }
            if (relationSet.any { otherEdge: Edge -> edge.source == otherEdge.source }) {
                return@loadSymbols Err(TranslationDTO("api.error.different-function-values", "function" to function))
            }
            relationSet.add(edge)
        }
        edge.relations.forEach { relation ->
            symbolTypes.putIfAbsent(relation, "P-2")
            if (symbolTypes[relation] != "P-2") {
                return@loadSymbols Err(TranslationDTO("api.error.different-arities", "symbol" to relation))
            }
            val relationSet = binarySymbols.getOrPut(relation) { mutableSetOf() }
            relationSet.add(edge)
        }
    }
    return Ok(SymbolTable(unarySymbols = unarySymbols, binarySymbols = binarySymbols, symbolTypes = symbolTypes))
}

/**
 * Iterates over the formula and adds new symbols to a symbol table.
 */
private fun FormulaHead.loadSymbols(symbolTable: SymbolTable): Result<SymbolTable, TranslationDTO> {
    val unarySymbols = symbolTable.unarySymbols.toMutableMap()
    val binarySymbols = symbolTable.binarySymbols.toMutableMap()
    val symbolTypes = symbolTable.symbolTypes.toMutableMap()
    this.symbolTable.forEach { (symbol: String, type: String) ->
        symbolTypes.putIfAbsent(symbol, type)
        if (type == "P-1" || type == "F-0") {
            unarySymbols.putIfAbsent(symbol, HashSet())
        } else if (type == "P-2" || type == "F-1") {
            binarySymbols.putIfAbsent(symbol, HashSet())
        }
        val typeInGraph = symbolTypes[symbol]
        if (type != typeInGraph) { // types are different?
            if (type == "V") {
                return@loadSymbols Err(TranslationDTO("api.error.bound-variable-reuse", "symbol" to symbol))
            } else {
                return@loadSymbols Err(TranslationDTO("api.error.different-arities-formula", "symbol" to symbol))
            }
        }
    }
    return Ok(SymbolTable(unarySymbols = unarySymbols, binarySymbols = binarySymbols, symbolTypes = symbolTypes))
}

/**
 * Functions mus be left total. Therefore this method checks if all function symbols are defined for all inputs.
 */
private fun checkTotality(graph: Graph, symbolTable: SymbolTable): Result<SymbolTable, TranslationDTO> {
    symbolTable.symbolTypes.forEach { (symbol: String, type: String) ->
        when (type) {
            "F-0" -> if (symbolTable.unarySymbols[symbol]!!.size != 1) {
                return@checkTotality Err(TranslationDTO("api.error.undefined-constant", "constant" to symbol))
            }
            "F-1" -> {
                val relationSet: Set<Edge> = symbolTable.binarySymbols[symbol]!!
                graph.nodes.forEach { node: Node ->
                    if (relationSet.none { edge: Edge -> edge.source == node }) {
                        return@checkTotality Err(TranslationDTO("api.error.function-totality", "function" to symbol))
                    }
                }
            }
        }
    }
    return Ok(symbolTable)
}
