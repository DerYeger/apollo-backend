package eu.yeger.gramofo.fol

import com.github.michaelbull.result.*
import com.github.michaelbull.result.binding
import eu.yeger.gramofo.fol.formula.*
import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.INFIX_EQUALITY
import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO
import java.util.*
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.any
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.none
import kotlin.collections.set
import kotlin.collections.toSet

typealias ModelCheckerResult = Result<ModelCheckerTrace, TranslationDTO>

private val infixPredicates = Settings[Settings.INFIX_PRED].toSet()

data class ModelCheckerTrace(
    val formula: String,
    val description: TranslationDTO,
    val isModel: Boolean,
    val shouldBeModel: Boolean,
    val children: List<ModelCheckerTrace>,
)

private data class SymbolTable(
    val unarySymbols: Map<String, Set<Node>>,
    val binarySymbols: Map<String, Set<Edge>>,
    val symbolTypes: Map<String, String>,
)

private class ModelCheckException(message: String) : RuntimeException(message)

fun checkModel(graph: Graph, formulaHead: FOLFormulaHead): ModelCheckerResult = binding {
    val symbolTable = graph.loadSymbols()
        .andThen { symbolTable -> formulaHead.loadSymbols(symbolTable) }
        .andThen { symbolTable -> checkTotality(graph, symbolTable) }.bind()
    runCatching { formulaHead.formula.checkFormula(graph, symbolTable, emptyMap(), true) }
        .mapError { error -> TranslationDTO(error.message ?: "api.error.unknown") }
        .bind()
}

private fun FOLFormula.checkFormula(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    return when (type) {
        FOLType.ForAll -> checkForAll(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.Exists -> checkExists(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.Not -> checkNot(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.And -> checkAnd(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.Or -> checkOr(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.Implication -> checkImplication(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.BiImplication -> checkBiImplication(graph, symbolTable, variableAssignments, shouldBeModel)
        FOLType.Predicate -> checkRelation(symbolTable, variableAssignments, shouldBeModel)
        FOLType.Constant -> checkConstant(variableAssignments, shouldBeModel)
        else -> throw ModelCheckException("[ModelChecker][Internal error] Unknown FOLFormula-Type: $type")
    }
}

private fun FOLFormula.checkForAll(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val variableName = getChildAt(0).name
    val children = graph.nodes.map { node: Node ->
        getChildAt(1).checkFormula(graph, symbolTable, variableAssignments + (variableName to node), shouldBeModel)
    }
    return if (children.all(ModelCheckerTrace::isModel)) {
        validated(TranslationDTO("api.forall.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
    } else {
        invalidated(TranslationDTO("api.forall.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
    }
}

private fun FOLFormula.checkExists(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val variableName = getChildAt(0).name
    val children = graph.nodes.map { node: Node ->
        getChildAt(1).checkFormula(graph, symbolTable, variableAssignments + (variableName to node), shouldBeModel)
    }
    return if (children.any(ModelCheckerTrace::isModel)) {
        validated(TranslationDTO("api.exists.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
    } else {
        invalidated(TranslationDTO("api.exists.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
    }
}

private fun FOLFormula.checkNot(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val child = getChildAt(0).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel.not())
    return when (child.isModel) {
        true -> invalidated(TranslationDTO("api.not.invalid"), variableAssignments, shouldBeModel, child)
        false -> validated(TranslationDTO("api.not.valid"), variableAssignments, shouldBeModel, child)
    }
}

private fun FOLFormula.checkAnd(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val left = getChildAt(0).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    val right = getChildAt(1).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    return when {
        left.isModel && right.isModel -> validated(TranslationDTO("api.and.both"), variableAssignments, shouldBeModel, left, right)
        left.isModel.not() && right.isModel.not() -> invalidated(TranslationDTO("api.and.neither"), variableAssignments, shouldBeModel, left, right)
        left.isModel.not() -> invalidated(TranslationDTO("api.and.left"), variableAssignments, shouldBeModel, left, right)
        else -> invalidated(TranslationDTO("api.and.right"), variableAssignments, shouldBeModel, left, right)
    }
}

private fun FOLFormula.checkOr(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val left = getChildAt(0).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    val right = getChildAt(1).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    return when {
        left.isModel && right.isModel -> validated(TranslationDTO("api.or.both"), variableAssignments, shouldBeModel, left, right)
        left.isModel -> validated(TranslationDTO("api.or.left"), variableAssignments, shouldBeModel, left, right)
        right.isModel -> validated(TranslationDTO("api.or.right"), variableAssignments, shouldBeModel, left, right)
        else -> invalidated(TranslationDTO("api.or.neither"), variableAssignments, shouldBeModel, left, right)
    }
}

private fun FOLFormula.checkImplication(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val left = getChildAt(0).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    val right = getChildAt(1).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    return when {
        right.isModel -> validated(TranslationDTO("api.implication.right"), variableAssignments, shouldBeModel, left, right)
        left.isModel.not() -> validated(TranslationDTO("api.implication.left"), variableAssignments, shouldBeModel, left, right)
        else -> invalidated(TranslationDTO("api.implication.invalid"), variableAssignments, shouldBeModel, left, right)
    }
}

private fun FOLFormula.checkBiImplication(
    graph: Graph,
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val left = getChildAt(0).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    val right = getChildAt(1).checkFormula(graph, symbolTable, variableAssignments, shouldBeModel)
    return when (left.isModel) {
        right.isModel -> validated(TranslationDTO("api.bi-implication.valid"), variableAssignments, shouldBeModel, left, right)
        else -> invalidated(TranslationDTO("api.bi-implication.invalid"), variableAssignments, shouldBeModel, left, right)
    }
}

private fun FOLFormula.checkRelation(
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    return when (children.size) {
        1 -> checkUnaryRelation(symbolTable, variableAssignments, shouldBeModel)
        2 -> checkBinaryRelation(symbolTable, variableAssignments, shouldBeModel)
        else -> throw ModelCheckException("[ModelChecker][Internal error] Found predicate with to many children.")
    }
}

private fun FOLFormula.checkUnaryRelation(
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val node = getChildAt(0).interpret(symbolTable, variableAssignments)
    val translationParams = mapOf("relation" to name, "node" to node.name)
    return when (symbolTable.unarySymbols[name]!!.contains(node)) {
        true -> validated(TranslationDTO("api.relation.unary.valid", translationParams), variableAssignments, shouldBeModel)
        false -> invalidated(TranslationDTO("api.relation.unary.invalid", translationParams), variableAssignments, shouldBeModel)
    }
}

private fun FOLFormula.checkBinaryRelation(
    symbolTable: SymbolTable,
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    val left = getChildAt(0).interpret(symbolTable, variableAssignments)
    val right = getChildAt(1).interpret(symbolTable, variableAssignments)
    val translationParams = mapOf(
        "firstTerm" to getChildAt(0).toString(variableAssignments),
        "secondTerm" to getChildAt(1).toString(variableAssignments),
        "firstResult" to left.name,
        "secondResult" to right.name
    )
    return if (name == INFIX_EQUALITY) {
        when (left) {
            right -> validated(TranslationDTO("api.relation.equality.valid", translationParams), variableAssignments, shouldBeModel)
            else -> invalidated(TranslationDTO("api.relation.equality.invalid", translationParams), variableAssignments, shouldBeModel)
        }
    } else {
        val binaryTranslationParams = translationParams + ("relation" to name)
        when (symbolTable.binarySymbols[name]!!.any { edge: Edge -> edge.source == left && edge.target == right }) {
            true -> validated(TranslationDTO("api.relation.binary.valid", binaryTranslationParams), variableAssignments, shouldBeModel)
            else -> invalidated(TranslationDTO("api.relation.binary.invalid", binaryTranslationParams), variableAssignments, shouldBeModel)
        }
    }
}

private fun FOLFormula.checkConstant(
    variableAssignments: Map<String, Node>,
    shouldBeModel: Boolean,
): ModelCheckerTrace {
    return when (name == FOLFormula.TT) {
        true -> validated(TranslationDTO("api.constant.true"), variableAssignments, shouldBeModel)
        else -> invalidated(TranslationDTO("api.constant.false"), variableAssignments, shouldBeModel)
    }
}

/**
 * Iterates over the graph and puts all found symbols in a symbol table.
 */
private fun Graph.loadSymbols(): Result<SymbolTable, TranslationDTO> {
    val unarySymbols = mutableMapOf<String, MutableSet<Node>>()
    val binarySymbols = mutableMapOf<String, MutableSet<Edge>>()
    val symbolTypes = mutableMapOf<String, String>()
    nodes.forEach { node: Node ->
        node.stringAttachments.forEach { symbol: String ->
            val symbolType = if (Character.isUpperCase(symbol[0])) "P-1" else "F-0"
            symbolTypes[symbol] = symbolType
            val relationSet = unarySymbols.getOrDefault(symbol, HashSet())
            if (symbolType == "F-0" && relationSet.size != 0) {
                return@loadSymbols Err(TranslationDTO(key = "api.error.duplicate-constant", "constant" to symbol))
            }
            relationSet.add(node)
            unarySymbols[symbol] = relationSet
        }
    }
    edges.forEach { edge: Edge ->
        edge.stringAttachments.forEach { symbol: String ->
            val symbolType =
                if (Character.isUpperCase(symbol[0]) || infixPredicates.contains(symbol)) "P-2" else "F-1"
            symbolTypes.putIfAbsent(symbol, symbolType)
            if (symbolType != symbolTypes[symbol]) {
                return@loadSymbols Err(TranslationDTO("api.error.different-arities", "symbol" to symbol))
            }
            val relationSet = binarySymbols.getOrDefault(symbol, HashSet())
            if (symbolType == "F-1" && relationSet.any { otherEdge: Edge -> edge.source == otherEdge.source }) {
                return@loadSymbols Err(TranslationDTO("api.error.different-function-values", "function" to symbol))
            }
            relationSet.add(edge)
            binarySymbols[symbol] = relationSet
        }
    }
    return Ok(SymbolTable(unarySymbols = unarySymbols, binarySymbols = binarySymbols, symbolTypes = symbolTypes))
}

/**
 * Iterates over the formula and add new symbols to the symbol tables.
 */
private fun FOLFormulaHead.loadSymbols(symbolTable: SymbolTable): Result<SymbolTable, TranslationDTO> {
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

/**
 * Takes a FOLFunction or FOLVariable and return the associated node within this interpretation.
 * @return a node which is associated with this term.
 * @throws ModelCheckException if the data is invalid. This should not happen.
 */
@Throws(ModelCheckException::class)
private fun FOLFormula.interpret(symbolTable: SymbolTable, variableAssignments: Map<String, Node>): Node {
    return when (this) {
        is FOLFunction -> {
            when (children.size) {
                0 -> symbolTable.unarySymbols[name]!!.first()
                1 -> {
                    val childResult = getChildAt(0).interpret(symbolTable, variableAssignments)
                    symbolTable.binarySymbols[name]!!.first { edge: Edge -> edge.source == childResult }.target
                }
                else -> throw ModelCheckException("[ModelChecker][Internal error] Found function with to many children.")
            }
        }
        is FOLBoundVariable ->
            variableAssignments[name] ?: throw ModelCheckException("[ModelChecker][Internal error] Variable is not assigned.")
        else -> throw ModelCheckException("[ModelChecker][Internal error] Not a valid function or a variable.")
    }
}

private fun FOLFormula.validated(description: TranslationDTO, variableAssignments: Map<String, Node>, shouldBeModel: Boolean, vararg children: ModelCheckerTrace) =
    ModelCheckerTrace(
        formula = this.toString(variableAssignments),
        description = description,
        isModel = true,
        shouldBeModel = shouldBeModel,
        children = children.toList()
    )

private fun FOLFormula.invalidated(description: TranslationDTO, variableAssignments: Map<String, Node>, shouldBeModel: Boolean, vararg children: ModelCheckerTrace) =
    ModelCheckerTrace(
        formula = this.toString(variableAssignments),
        description = description,
        isModel = false,
        shouldBeModel = shouldBeModel,
        children = children.toList()
    )
