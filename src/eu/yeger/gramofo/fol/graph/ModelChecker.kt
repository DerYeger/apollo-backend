package eu.yeger.gramofo.fol.graph

import eu.yeger.gramofo.fol.Lang
import eu.yeger.gramofo.fol.Settings
import eu.yeger.gramofo.fol.formula.*
import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.INFIX_EQUALITY
import java.util.*

fun checkModel(graph: Graph, formulaHead: FOLFormulaHead, locale: Locale = Locale.ENGLISH): String? {
    return ModelChecker(graph, formulaHead, Lang(locale)).checkIfGraphIsModelFromFormula()
}

/**
 * This class is used to check if a graph is model of a formula.
 */
private class ModelChecker(
    private val graph: Graph,
    private val formulaHead: FOLFormulaHead,
    private val lang: Lang
) {
    private val oneArySymbolTable: MutableMap<String, MutableSet<Vertex>> = mutableMapOf()
    private val twoArySymbolTable: MutableMap<String, MutableSet<Edge>> = mutableMapOf()
    private val symbolTypeTable: MutableMap<String, String> = mutableMapOf()
    private val bindVariableValues: MutableMap<String, Vertex> = mutableMapOf()
    private val infixPredicates: Set<String> = Settings[Settings.INFIX_PRED].toSet()

    init {
        loadGraphSymbols()
        loadFormulaSymbols()
        checkTotality()
    }

    /**
     * Checks if the graph is a model of the formula.
     * @return null if the graph is model of the formula or a String containing a error message else.
     */
    fun checkIfGraphIsModelFromFormula(): String? {
        return try {
            if (checkModel(formulaHead.formula)) {
                null
            } else {
                "Not a model"
            }
        } catch (mce: ModelCheckException) {
            mce.message
        }
    }

    /**
     * Iterates over the graph and puts all found symbols in the symbol tables.
     * @throws ModelCheckException if a symbol is used with different arities within the graph and
     * if a 1-ary function is not right-unique
     */
    @Throws(ModelCheckException::class)
    private fun loadGraphSymbols() {
        graph.vertices.forEach { vertex: Vertex ->
            vertex.stringAttachments.forEach { symbol: String ->
                val symbolType = if (Character.isUpperCase(symbol[0])) "P-1" else "F-0"
                symbolTypeTable[symbol] = symbolType
                val relationSet = oneArySymbolTable.getOrDefault(symbol, HashSet())
                if (symbolType == "F-0" && relationSet.size != 0) {
                    throw ModelCheckException("The 0-ary function symbol '$symbol' can only be assigned to one vertex.")
                }
                relationSet.add(vertex)
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
                    throw ModelCheckException("The 1-ary function '$symbol' has at least for one vertex two function values. A function must be right-unique.")
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
                    throw ModelCheckException(
                        "The symbol '" + symbol + "' is defined in the formula as a bound variable " +
                            "but in the graph it is a function symbol. You cannot use one symbol twice for different use cases."
                    )
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
                    graph.vertices.forEach { vertex: Vertex ->
                        if (relationSet.none { edge: Edge -> edge.source == vertex }) {
                            throw ModelCheckException("The 1-ary function '$symbol' must be total. Please be sure that it is defined for all vertexes.")
                        }
                    }
                }
            }
        }
    }

    /**
     * This function does the main job: the model checking. It handles the top-level type of the given formula
     * and call it self recursively on all children.<br></br>
     * The base case are: <br></br>
     * - the bind variables, which are associated with an real element throw an quantifier before<br></br>
     * - 0-are function symbols, which must be specified throw the graph <br></br>
     * Predicates and function interpretation are given throw the graph too.<br></br>
     * The Equality is simply implemented as the equality of two vertexes: vertexFromChild1 == vertexFromChild2 ?.
     * This is possible, because every term can be interpreted with a vertex.
     * @throws ModelCheckException if the data is invalid. This should not happen.
     */
    @Throws(ModelCheckException::class)
    private fun checkModel(formula: FOLFormula): Boolean {
        return when (formula.type) {
            FOLType.ForAll -> graph.vertices.all { vertex: Vertex ->
                bindVariableValues[formula.getChildAt(0).name] = vertex
                checkModel(formula.getChildAt(1))
            }
            FOLType.Exists -> graph.vertices.any { vertex: Vertex ->
                bindVariableValues[formula.getChildAt(0).name] = vertex
                checkModel(formula.getChildAt(1))
            }
            FOLType.Not -> !checkModel(formula.getChildAt(0))
            FOLType.And -> checkModel(formula.getChildAt(0)) && checkModel(formula.getChildAt(1))
            FOLType.Or -> checkModel(formula.getChildAt(0)) || checkModel(formula.getChildAt(1))
            FOLType.Implication -> !checkModel(formula.getChildAt(0)) || checkModel(formula.getChildAt(1))
            FOLType.BiImplication -> {
                val left = checkModel(formula.getChildAt(0))
                val right = checkModel(formula.getChildAt(1))
                left && right || !left && !right
            }
            FOLType.Predicate -> when (formula.children.size) {
                1 -> oneArySymbolTable[formula.name]!!.contains(interpret(formula.getChildAt(0)))
                2 ->
                    if (formula.name == INFIX_EQUALITY) {
                        interpret(formula.getChildAt(0)) == interpret(formula.getChildAt(1))
                    } else {
                        twoArySymbolTable[formula.name]!!.any { edge: Edge ->
                            edge.source == interpret(formula.getChildAt(0)) && edge.target == interpret(
                                formula.getChildAt(1)
                            )
                        }
                    }
                else -> throw ModelCheckException("[ModelChecker][Internal error] Found predicate with to many children.")
            }
            FOLType.Constant -> FOLFormula.TT == formula.name
            else -> throw ModelCheckException("[ModelChecker][Internal error] Unknown FOLFormula-Type: " + formula.type)
        }
    }

    /**
     * Takes a FOLFunction or FOLVariable and return the associated vertex within this interpretation.
     * @param symbol mus be a FOLFunction or an FOLVariable
     * @return a vertex which is associated with this term.
     * @throws ModelCheckException if the data is invalid. This should not happen.
     */
    @Throws(ModelCheckException::class)
    private fun interpret(symbol: FOLFormula): Vertex? {
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
            is FOLBoundVariable -> {
                if (bindVariableValues[symbol.name] == null) {
                    throw ModelCheckException("[ModelChecker][Internal error] No bind value found for variable.")
                }
                bindVariableValues[symbol.name]
            }
            else -> throw ModelCheckException("[ModelChecker][Internal error] Not a valid function or a variable.")
        }
    }

    class ModelCheckException(message: String) : RuntimeException(message)
}
