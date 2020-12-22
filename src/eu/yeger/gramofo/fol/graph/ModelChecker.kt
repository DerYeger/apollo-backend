package eu.yeger.gramofo.fol.graph

import eu.yeger.gramofo.fol.FOLParser
import eu.yeger.gramofo.fol.Settings
import eu.yeger.gramofo.fol.formula.*
import eu.yeger.gramofo.fol.formula.FOLFormula.INFIX_EQUALITY
import java.util.*
import java.util.function.Consumer

/**
 * This class is used to check if the drawn graph is a model of the formula. Both are available throw the datamodel.
 * (please be patient I wrote this drunk =). I think its a little bit necessary for this theory stuff)
 */
class ModelChecker {
    private var graph: Graph? = null
    private var formulaHead: FOLFormulaHead? = null
    private var oneArySymbolTable: HashMap<String, MutableSet<Vertex?>>? = null
    private var twoArySymbolTable: HashMap<String, MutableSet<Edge>>? = null
    private var symbolTypeTable: HashMap<String, String>? = null
    private var bindVariableValues: HashMap<String, Vertex?>? = null
    private var infixPredicates: HashSet<String>? = null

    /**
     * Checks if the graph, which is drawn, is a model from the formula.
     * @param graph the interpretation as a graph.
     * @param formulaHead the formula to be checked
     * @return null if the graph is model of the formula and a String containing a error message else.
     */
    fun checkIfGraphIsModelFromFormula(graph: Graph?, formulaHead: FOLFormulaHead): String? {
        this.graph = graph
        this.formulaHead = formulaHead
        oneArySymbolTable = HashMap()
        twoArySymbolTable = HashMap()
        symbolTypeTable = HashMap()
        bindVariableValues = HashMap()
        infixPredicates = HashSet(Arrays.asList(*FOLParser().settings.getSetting(Settings.INFIX_PRED)))
        return try {
            checkIfInputIsValid()
            loadGraphSymbols()
            loadFormulaSymbols()
            checkTotality()
            if (checkModel(formulaHead.formula)) {
                null
            } else {
                ""
            }
        } catch (mce: ModelCheckException) {
            mce.message
        }
    }

    fun checkModel(graph: Graph?, formulaHead: FOLFormulaHead): Boolean {
        val result = checkIfGraphIsModelFromFormula(
            graph,
            formulaHead
        )
        return result == null || result != ""
    }

    @Throws(ModelCheckException::class)
    private fun checkIfInputIsValid() {
        if (graph == null) {
            throw ModelCheckException("[ModelChecker][Internal error] graph is null.")
        }
        if (formulaHead == null) {
            throw ModelCheckException("You must parse a formula first")
        }
        if (graph!!.vertices.size < 1) {
            throw ModelCheckException(
                "The graph must contain at least one element. This is because the vertexes represent " +
                    "the universe and a universe must contain at least one element."
            )
        }
    }

    /**
     * Iterates over the graph and puts all found symbols in the symbol tables.
     * @throws ModelCheckException if a symbol is used with different arities within the graph and
     * if a 1-ary function is not right-unique
     */
    @Throws(ModelCheckException::class)
    private fun loadGraphSymbols() {
        graph!!.vertices.forEach(
            Consumer { vertex: Vertex ->
                vertex.stringAttachments.forEach(
                    Consumer { symbol: String ->
                        val symbolType = if (Character.isUpperCase(symbol[0])) "P-1" else "F-0"
                        symbolTypeTable!![symbol] = symbolType
                        val relationSet = oneArySymbolTable!!.getOrDefault(symbol, HashSet())
                        if (symbolType == "F-0" && relationSet.size != 0) {
                            throw ModelCheckException("The 0-ary function symbol '$symbol' can only be assigned to one vertex.")
                        }
                        relationSet.add(vertex)
                        oneArySymbolTable!![symbol] = relationSet
                    }
                )
            }
        )
        graph!!.edges.forEach(
            Consumer { edge: Edge ->
                edge.stringAttachments.forEach(
                    Consumer { symbol: String ->
                        val symbolType =
                            if (Character.isUpperCase(symbol[0]) || infixPredicates!!.contains(symbol)) "P-2" else "F-1"
                        symbolTypeTable!!.putIfAbsent(symbol, symbolType)
                        if (symbolType != symbolTypeTable!![symbol]) {
                            throw ModelCheckException("The symbol '$symbol' is defined with different arities within the graph.")
                        }
                        val relationSet = twoArySymbolTable!!.getOrDefault(symbol, HashSet())
                        if (symbolType == "F-1" && relationSet.stream()
                            .anyMatch { otherEdge: Edge -> edge.source == otherEdge.source }
                        ) {
                            throw ModelCheckException("The 1-ary function '$symbol' has at least for one vertex two function values. A function must be right-unique.")
                        }
                        relationSet.add(edge)
                        twoArySymbolTable!![symbol] = relationSet
                    }
                )
            }
        )
    }

    /**
     * Iterates over the formula and add new symbols to the symbol tables.
     * @throws ModelCheckException if there are symbols used with different meanings
     */
    @Throws(ModelCheckException::class)
    private fun loadFormulaSymbols() {
        formulaHead!!.symbolTable.forEach { (symbol: String, type: String) ->
            symbolTypeTable!!.putIfAbsent(symbol, type)
            if (type == "P-1" || type == "F-0") {
                oneArySymbolTable!!.putIfAbsent(symbol, HashSet())
            } else if (type == "P-2" || type == "F-1") {
                twoArySymbolTable!!.putIfAbsent(symbol, HashSet())
            }
            val typeInGraph = symbolTypeTable!![symbol]
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
     * Functions mus be left total. Therefor this method checks all function symbols, if they are defined for all inputs.
     * @throws ModelCheckException if some function symbols aren't defined for all inputs.
     */
    @Throws(ModelCheckException::class)
    private fun checkTotality() {
        symbolTypeTable!!.forEach { (symbol: String, type: String?) ->
            when (type) {
                "F-0" -> if (oneArySymbolTable!![symbol]!!.size != 1) {
                    throw ModelCheckException("The 0-ary function '$symbol' must be defined. Please add it to the graph.")
                }
                "F-1" -> {
                    val relationSet: Set<Edge> = twoArySymbolTable!![symbol]!!
                    graph!!.vertices.forEach(
                        Consumer { vertex: Vertex ->
                            if (relationSet.stream().noneMatch { edge: Edge -> edge.source == vertex }) {
                                throw ModelCheckException("The 1-ary function '$symbol' must be total. Please be sure that it is defined for all vertexes.")
                            }
                        }
                    )
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
        // note: this could also be done with inheritance. This would maybe the cleaner solution but I did not want to mix this could wit the datamodel.
        // Therefor I decide to make a switch case
        return when (formula.type) {
            FOLType.ForAll -> graph!!.vertices.stream().allMatch { vertex: Vertex? ->
                bindVariableValues!![formula.getChildAt(0).name] = vertex
                checkModel(formula.getChildAt(1))
            }
            FOLType.Exists -> graph!!.vertices.stream().anyMatch { vertex: Vertex? ->
                bindVariableValues!![formula.getChildAt(0).name] = vertex
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
                1 ->
                    oneArySymbolTable!![formula.name]!!
                        .contains(interpret(formula.getChildAt(0)))
                2 ->
                    if (formula.name == INFIX_EQUALITY) {
                        interpret(formula.getChildAt(0)) == interpret(formula.getChildAt(1))
                    } else {
                        twoArySymbolTable!![formula.name]!!.stream().anyMatch { edge: Edge ->
                            edge.source == interpret(formula.getChildAt(0)) && edge.target == interpret(
                                formula.getChildAt(
                                    1
                                )
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
        return if (symbol is FOLFunction) {
            if (symbol.getChildren().size == 0) {
                oneArySymbolTable!![symbol.getName()]!!.stream().findAny().get()
            } else if (symbol.getChildren().size == 1) {
                val childResult = interpret(symbol.getChildAt(0))
                twoArySymbolTable!![symbol.getName()]!!.stream()
                    .filter { edge: Edge -> edge.source == childResult }
                    .findAny().get().target
            } else {
                throw ModelCheckException("[ModelChecker][Internal error] Found function with to many children.")
            }
        } else if (symbol is FOLBoundVariable) {
            if (bindVariableValues!![symbol.getName()] == null) {
                throw ModelCheckException("[ModelChecker][Internal error] No bind value found for variable.")
            }
            bindVariableValues!![symbol.getName()]
        } else {
            throw ModelCheckException("[ModelChecker][Internal error] Not a valid function or a variable.")
        }
    }
    // //////////////////////// helping stuff ///////////////////////////////
    /**
     * A custom exception to handle error cases happened in the model check.
     */
    class ModelCheckException
    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message. The detail message is saved for
     * later retrieval by the [.getMessage] method.
     */
    (message: String?) : RuntimeException(message)
}
