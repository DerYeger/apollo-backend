package eu.yeger.gramofo.fol

import com.github.michaelbull.result.*
import com.github.michaelbull.result.binding
import eu.yeger.gramofo.model.api.Feedback
import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.domain.fol.*
import eu.yeger.gramofo.model.dto.TranslationDTO

typealias ModelCheckerResult = Result<ModelCheckerTrace, TranslationDTO>

fun checkModel(graph: Graph, formulaHead: FormulaHead, feedback: Feedback): ModelCheckerResult = binding {
    val symbolTable = graph.loadSymbols()
        .andThen { symbolTable -> formulaHead.loadSymbols(symbolTable) }
        .andThen { symbolTable -> checkTotality(graph, symbolTable) }.bind()
    runCatching {
        when (feedback) {
            Feedback.full -> formulaHead.formula.fullCheck(graph, symbolTable, emptyMap(), true)
            Feedback.relevant -> formulaHead.formula.partialCheck(graph, symbolTable, emptyMap(), true)
            Feedback.minimal -> formulaHead.formula.partialCheck(graph, symbolTable, emptyMap(), true).copy(children = null)
        }
    }.mapError { error -> TranslationDTO(error.message ?: error.printStackTrace().let { "api.error.unknown" }) }.bind()
}

fun Formula.validated(description: TranslationDTO, variableAssignments: Map<String, Node>, shouldBeModel: Boolean, vararg children: ModelCheckerTrace) =
    ModelCheckerTrace(
        formula = this.toString(variableAssignments, false),
        description = description,
        isModel = true,
        shouldBeModel = shouldBeModel,
        children = children.toList().takeUnless { it.isEmpty() }
    )

fun Formula.invalidated(description: TranslationDTO, variableAssignments: Map<String, Node>, shouldBeModel: Boolean, vararg children: ModelCheckerTrace) =
    ModelCheckerTrace(
        formula = this.toString(variableAssignments, false),
        description = description,
        isModel = false,
        shouldBeModel = shouldBeModel,
        children = children.toList().takeUnless { it.isEmpty() }
    )

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
