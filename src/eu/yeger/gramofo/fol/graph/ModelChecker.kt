package eu.yeger.gramofo.fol.graph

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import eu.yeger.gramofo.fol.Lang
import eu.yeger.gramofo.fol.Settings
import eu.yeger.gramofo.fol.formula.*
import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.INFIX_EQUALITY
import eu.yeger.gramofo.model.api.TranslationDTO
import java.util.*
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.any
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.none
import kotlin.collections.set
import kotlin.collections.toSet

typealias ModelCheckerResult = Result<ModelCheckerTrace, String>

fun checkModel(graph: Graph, formulaHead: FOLFormulaHead, locale: Locale = Locale.ENGLISH): ModelCheckerResult {
    return ModelChecker(graph, formulaHead, Lang(locale)).result
}

/**
 * This class is used to check if a graph is model of a formula.
 */
private class ModelChecker(
    private val graph: Graph,
    private val formulaHead: FOLFormulaHead,
    private val lang: Lang
) {
    private val oneArySymbolTable: MutableMap<String, MutableSet<Node>> = mutableMapOf()
    private val twoArySymbolTable: MutableMap<String, MutableSet<Edge>> = mutableMapOf()
    private val symbolTypeTable: MutableMap<String, String> = mutableMapOf()
    private val bindVariableValues: MutableMap<String, Node> = mutableMapOf()
    private val infixPredicates: Set<String> = Settings[Settings.INFIX_PRED].toSet()

    val result: ModelCheckerResult

    init {
        loadGraphSymbols()
        loadFormulaSymbols()
        checkTotality()

        result = try {
            Ok(checkModel(formulaHead.formula))
        } catch (mce: ModelCheckException) {
            Err(mce.message ?: "An error occurred")
        }
    }

    /**
     * Iterates over the graph and puts all found symbols in the symbol tables.
     * @throws ModelCheckException if a symbol is used with different arities within the graph and
     * if a 1-ary function is not right-unique
     */
    @Throws(ModelCheckException::class)
    private fun loadGraphSymbols() {
        graph.nodes.forEach { node: Node ->
            node.stringAttachments.forEach { symbol: String ->
                val symbolType = if (Character.isUpperCase(symbol[0])) "P-1" else "F-0"
                symbolTypeTable[symbol] = symbolType
                val relationSet = oneArySymbolTable.getOrDefault(symbol, HashSet())
                if (symbolType == "F-0" && relationSet.size != 0) {
                    throw ModelCheckException("The 0-ary function symbol '$symbol' can only be assigned to one node.")
                }
                relationSet.add(node)
                oneArySymbolTable[symbol] = relationSet
            }
        }
        graph.edges.forEach { edge: Edge ->
            edge.stringAttachments.forEach { symbol: String ->
                val symbolType =
                    if (Character.isUpperCase(symbol[0]) || infixPredicates.contains(symbol)) "P-2" else "F-1"
                symbolTypeTable.putIfAbsent(symbol, symbolType)
                if (symbolType != symbolTypeTable[symbol]) {
                    throw ModelCheckException("The symbol '$symbol' is defined with different arities within the graph.")
                }
                val relationSet = twoArySymbolTable.getOrDefault(symbol, HashSet())
                if (symbolType == "F-1" && relationSet.any { otherEdge: Edge -> edge.source == otherEdge.source }) {
                    throw ModelCheckException("The 1-ary function '$symbol' has two function values for at least one node. A function must be right-unique.")
                }
                relationSet.add(edge)
                twoArySymbolTable[symbol] = relationSet
            }
        }
    }

    /**
     * Iterates over the formula and add new symbols to the symbol tables.
     * @throws ModelCheckException if there are symbols used with different meanings
     */
    @Throws(ModelCheckException::class)
    private fun loadFormulaSymbols() {
        formulaHead.symbolTable.forEach { (symbol: String, type: String) ->
            symbolTypeTable.putIfAbsent(symbol, type)
            if (type == "P-1" || type == "F-0") {
                oneArySymbolTable.putIfAbsent(symbol, HashSet())
            } else if (type == "P-2" || type == "F-1") {
                twoArySymbolTable.putIfAbsent(symbol, HashSet())
            }
            val typeInGraph = symbolTypeTable[symbol]
            if (type != typeInGraph) { // types are different?
                if (type == "V") {
                    throw ModelCheckException("The symbol '$symbol' is defined in the formula as a bound variable but in the graph it is a function symbol. You cannot use one symbol twice for different use cases.")
                } else {
                    throw ModelCheckException("The arity of the symbol '$symbol' in the graph differ from the arity used in the formula.")
                }
            }
        }
    }

    /**
     * Functions mus be left total. Therefore this method checks all function symbols, if they are defined for all inputs.
     * @throws ModelCheckException if some function symbols aren't defined for all inputs.
     */
    @Throws(ModelCheckException::class)
    private fun checkTotality() {
        symbolTypeTable.forEach { (symbol: String, type: String) ->
            when (type) {
                "F-0" -> if (oneArySymbolTable[symbol]!!.size != 1) {
                    throw ModelCheckException("The 0-ary function '$symbol' must be defined. Please add it to the graph.")
                }
                "F-1" -> {
                    val relationSet: Set<Edge> = twoArySymbolTable[symbol]!!
                    graph.nodes.forEach { node: Node ->
                        if (relationSet.none { edge: Edge -> edge.source == node }) {
                            throw ModelCheckException("The 1-ary function '$symbol' must be total. Please be sure that it is defined for all nodes.")
                        }
                    }
                }
            }
        }
    }

    private fun checkModel(formula: FOLFormula): ModelCheckerTrace {
        return when (formula.type) {
            FOLType.ForAll -> checkForAll(formula)
            FOLType.Exists -> checkExists(formula)
            FOLType.Not -> checkNot(formula)
            FOLType.And -> checkAnd(formula)
            FOLType.Or -> checkOr(formula)
            FOLType.Implication -> checkImplication(formula)
            FOLType.BiImplication -> checkBiImplication(formula)
            FOLType.Predicate -> checkPredicate(formula)
            FOLType.Constant -> checkConstant(formula)
            else -> throw ModelCheckException("[ModelChecker][Internal error] Unknown FOLFormula-Type: " + formula.type)
        }
    }

    /**
     * Takes a FOLFunction or FOLVariable and return the associated node within this interpretation.
     * @param symbol mus be a FOLFunction or an FOLVariable
     * @return a node which is associated with this term.
     * @throws ModelCheckException if the data is invalid. This should not happen.
     */
    @Throws(ModelCheckException::class)
    private fun interpret(symbol: FOLFormula): Node {
        return when (symbol) {
            is FOLFunction -> {
                when (symbol.children.size) {
                    0 -> oneArySymbolTable[symbol.name]!!.first()
                    1 -> {
                        val childResult = interpret(symbol.getChildAt(0))
                        twoArySymbolTable[symbol.name]!!.first { edge: Edge -> edge.source == childResult }.target
                    }
                    else -> throw ModelCheckException("[ModelChecker][Internal error] Found function with to many children.")
                }
            }
            is FOLBoundVariable ->
                bindVariableValues[symbol.name] ?: throw ModelCheckException("[ModelChecker][Internal error] No bind value found for variable.")
            else -> throw ModelCheckException("[ModelChecker][Internal error] Not a valid function or a variable.")
        }
    }

    class ModelCheckException(message: String) : RuntimeException(message)

    private fun checkForAll(formula: FOLFormula): ModelCheckerTrace {
        val variableName = formula.getChildAt(0).name
        val children = graph.nodes.map { node: Node ->
            bindVariableValues[variableName] = node
            checkModel(formula.getChildAt(1))
        }
        bindVariableValues.remove(variableName)
        return if (children.all(ModelCheckerTrace::isModel)) {
            formula.validated(TranslationDTO("api.forall.valid"), *children.toTypedArray())
        } else {
            formula.invalidated(TranslationDTO("api.forall.invalid"), *children.toTypedArray())
        }
    }

    private fun checkExists(formula: FOLFormula): ModelCheckerTrace {
        val variableName = formula.getChildAt(0).name
        val children = graph.nodes.map { node: Node ->
            bindVariableValues[variableName] = node
            checkModel(formula.getChildAt(1))
        }
        bindVariableValues.remove(variableName)
        return if (children.any(ModelCheckerTrace::isModel)) {
            formula.validated(TranslationDTO("api.exists.valid"), *children.toTypedArray())
        } else {
            formula.invalidated(TranslationDTO("api.exists.invalid"), *children.toTypedArray())
        }
    }

    private fun checkNot(formula: FOLFormula): ModelCheckerTrace {
        val result = checkModel(formula.getChildAt(0))
        return when (result.isModel) {
            true -> formula.invalidated(TranslationDTO("api.not.invalid"), result)
            false -> formula.validated(TranslationDTO("api.not.valid"), result)
        }
    }

    private fun checkAnd(formula: FOLFormula): ModelCheckerTrace {
        val left = checkModel(formula.getChildAt(0))
        val right = checkModel(formula.getChildAt(1))
        return when {
            left.isModel && right.isModel -> formula.validated(
                TranslationDTO("api.and.both"),
                left,
                right
            )
            left.isModel.not() && right.isModel.not() -> formula.invalidated(TranslationDTO("api.and.neither"), left, right)
            left.isModel.not() -> formula.invalidated(TranslationDTO("api.and.left"), left)
            else -> formula.invalidated(TranslationDTO("api.and.right"), right)
        }
    }

    private fun checkOr(formula: FOLFormula): ModelCheckerTrace {
        val left = checkModel(formula.getChildAt(0))
        val right = checkModel(formula.getChildAt(1))
        return when {
            left.isModel && right.isModel -> formula.validated(
                TranslationDTO("api.or.both"),
                left,
                right
            )
            left.isModel -> formula.validated(TranslationDTO("api.or.left"), left)
            right.isModel -> formula.validated(TranslationDTO("api.or.right"), right)
            else -> formula.invalidated(TranslationDTO("api.or.neither"), left, right)
        }
    }

    private fun checkImplication(formula: FOLFormula): ModelCheckerTrace {
        val left = checkModel(formula.getChildAt(0))
        val right = checkModel(formula.getChildAt(1))
        return when {
            right.isModel -> formula.validated(TranslationDTO("api.implication.right"), right)
            left.isModel.not() -> formula.validated(TranslationDTO("api.implication.left"), left)
            else -> formula.invalidated(TranslationDTO("api.implication.invalid"), left, right)
        }
    }

    private fun checkBiImplication(formula: FOLFormula): ModelCheckerTrace {
        val left = checkModel(formula.getChildAt(0))
        val right = checkModel(formula.getChildAt(1))
        return when (left.isModel) {
            right.isModel -> formula.validated(TranslationDTO("api.bi-implication.valid"), left, right)
            else -> formula.invalidated(TranslationDTO("api.bi-implication.invalid"), left, right)
        }
    }

    private fun checkPredicate(formula: FOLFormula): ModelCheckerTrace {
        return when (formula.children.size) {
            1 -> checkUnaryPredicate(formula)
            2 -> checkBinaryPredicate(formula)
            else -> throw ModelCheckException("[ModelChecker][Internal error] Found predicate with to many children.")
        }
    }

    private fun checkUnaryPredicate(formula: FOLFormula): ModelCheckerTrace {
        val node = interpret(formula.getChildAt(0))
        val translationParams = mapOf("relation" to formula.name, "node" to node.name)
        return when (oneArySymbolTable[formula.name]!!.contains(node)) {
            true -> formula.validated(TranslationDTO("api.relation.unary.valid", translationParams))
            false -> formula.invalidated(TranslationDTO("api.relation.unary.invalid", translationParams))
        }
    }

    private fun checkBinaryPredicate(formula: FOLFormula): ModelCheckerTrace {
        val left = interpret(formula.getChildAt(0))
        val right = interpret(formula.getChildAt(1))
        val translationParams = mapOf("first" to left.name, "second" to right.name)
        return if (formula.name == INFIX_EQUALITY) {
            when (left) {
                right -> formula.validated(TranslationDTO("api.relation.equality.valid", translationParams))
                else -> formula.invalidated(TranslationDTO("api.relation.equality.invalid", translationParams))
            }
        } else {
            val binaryTranslationParams = translationParams + ("relation" to formula.name)
            when (twoArySymbolTable[formula.name]!!.any { edge: Edge -> edge.source == left && edge.target == right }) {
                true -> formula.validated(TranslationDTO("api.relation.binary.valid", binaryTranslationParams))
                else -> formula.invalidated(TranslationDTO("api.relation.binary.invalid", binaryTranslationParams))
            }
        }
    }

    private fun checkConstant(formula: FOLFormula): ModelCheckerTrace {
        return when (FOLFormula.TT == formula.name) {
            true -> formula.validated(TranslationDTO("api.constant.true"))
            else -> formula.invalidated(TranslationDTO("api.constant.false"))
        }
    }

    private fun FOLFormula.validated(description: TranslationDTO, vararg children: ModelCheckerTrace) =
        ModelCheckerTrace(this.toString(bindVariableValues), description, true, children.toList())

    private fun FOLFormula.invalidated(description: TranslationDTO, vararg children: ModelCheckerTrace) =
        ModelCheckerTrace(this.toString(bindVariableValues), description, false, children.toList())
}
