package eu.yeger.gramofo.fol.graph;

import eu.yeger.gramofo.fol.FOLParser;
import eu.yeger.gramofo.fol.Settings;
import eu.yeger.gramofo.fol.formula.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * This class is used to check if the drawn graph is a model of the formula. Both are available throw the datamodel.
 * (please be patient I wrote this drunk =). I think its a little bit necessary for this theory stuff)
 */
public class ModelChecker {

    private Graph graph;
    private FOLFormulaHead formulaHead;
    private HashMap<String, Set<Vertex>> oneArySymbolTable;
    private HashMap<String, Set<Edge>> twoArySymbolTable;
    private HashMap<String, String> symbolTypeTable;
    private HashMap<String, Vertex> bindVariableValues;
    private HashSet<String> infixPredicates;



    /**
     * Checks if the graph, which is drawn, is a model from the formula.
     * @param graph the interpretation as a graph.
     * @param formulaHead the formula to be checked
     * @return null if the graph is model of the formula and a String containing a error message else.
     */
    public String checkIfGraphIsModelFromFormula(Graph graph, FOLFormulaHead formulaHead) {
        this.graph = graph;
        this.formulaHead = formulaHead;
        this.oneArySymbolTable  = new HashMap<>();
        this.twoArySymbolTable  = new HashMap<>();
        this.symbolTypeTable    = new HashMap<>();
        this.bindVariableValues = new HashMap<>();
        this.infixPredicates    = new HashSet<>( Arrays.asList(new FOLParser().getSettings().getSetting(Settings.INFIX_PRED)) );

        try {
            checkIfInputIsValid();
            loadGraphSymbols();
            loadFormulaSymbols();
            checkTotality();

            if (checkModel(formulaHead.getFormula())) {
                return null;
            } else {
                return "";
            }
        } catch (ModelCheckException mce) {
            return mce.getMessage();
        }
    }

    public boolean checkModel(Graph graph, FOLFormulaHead formulaHead) {
        String result = checkIfGraphIsModelFromFormula(
                graph,
                formulaHead
        );

        return result == null || !result.equals("");
    }


    private void checkIfInputIsValid() throws ModelCheckException {
        if(graph == null) {
            throw new ModelCheckException("[ModelChecker][Internal error] graph is null.");
        }
        if (formulaHead == null) {
            throw new ModelCheckException("You must parse a formula first");
        }

        if (graph.getVertexes().size() < 1) {
            throw new ModelCheckException("The graph must contain at least one element. This is because the vertexes represent " +
                "the universe and a universe must contain at least one element.");
        }
    }


    /**
     * Iterates over the graph and puts all found symbols in the symbol tables.
     * @throws ModelCheckException if a symbol is used with different arities within the graph and
     * if a 1-ary function is not right-unique
     */
    private void loadGraphSymbols() throws ModelCheckException {
        graph.getVertexes().forEach( vertex ->
            vertex.getStringAttachments().forEach(symbol -> {
                String symbolType = Character.isUpperCase(symbol.charAt(0)) ? "P-1" : "F-0";
                symbolTypeTable.put(symbol, symbolType);
                Set<Vertex> relationSet = oneArySymbolTable.getOrDefault(symbol, new HashSet<>());
                if( symbolType.equals("F-0") && relationSet.size() != 0) {
                    throw new ModelCheckException("The 0-ary function symbol '" + symbol + "' can only be assigned to one vertex.");
                }
                relationSet.add(vertex);
                oneArySymbolTable.put(symbol,relationSet);
            })
        );

        graph.getEdges().forEach(edge ->
            edge.getStringAttachments().forEach(symbol -> {
                String symbolType = Character.isUpperCase(symbol.charAt(0)) || infixPredicates.contains(symbol) ? "P-2" : "F-1";
                symbolTypeTable.putIfAbsent(symbol, symbolType);
                if (!symbolType.equals(symbolTypeTable.get(symbol))) {
                    throw new ModelCheckException("The symbol '" + symbol + "' is defined with different arities within the graph.");
                }
                Set<Edge> relationSet = twoArySymbolTable.getOrDefault(symbol, new HashSet<>());
                if( symbolType.equals("F-1") && relationSet.stream().anyMatch( otherEdge -> edge.getFromVertex() == otherEdge.getFromVertex() )) {
                    throw new ModelCheckException("The 1-ary function '" + symbol + "' has at least for one vertex two function values. A function must be right-unique.");
                }
                relationSet.add(edge);
                twoArySymbolTable.put(symbol,relationSet);
            })
        );
    }

    /**
     * Iterates over the formula and add new symbols to the symbol tables.
     * @throws ModelCheckException if there are symbols used with different meanings
     */
    private void loadFormulaSymbols() throws ModelCheckException {
        formulaHead.getSymbolTable().forEach( (symbol, type) -> {
            symbolTypeTable.putIfAbsent(symbol, type);
            if (type.equals("P-1") || type.equals("F-0")) {
               oneArySymbolTable.putIfAbsent(symbol, new HashSet<>());
            } else if (type.equals("P-2") || type.equals("F-1")) {
               twoArySymbolTable.putIfAbsent(symbol, new HashSet<>());
            }
            String typeInGraph = symbolTypeTable.get(symbol);
            if (!type.equals(typeInGraph)) { // types are different?
                if (type.equals("V")) {
                    throw new ModelCheckException("The symbol '" + symbol + "' is defined in the formula as a bound variable " +
                        "but in the graph it is a function symbol. You cannot use one symbol twice for different use cases.");
                } else {
                    throw new ModelCheckException("The arity of the symbol '" + symbol + "' in the graph differ from the arity used in the formula.");
                }
            }
        });
    }


    /**
     * Functions mus be left total. Therefor this method checks all function symbols, if they are defined for all inputs.
     * @throws ModelCheckException if some function symbols aren't defined for all inputs.
     */
    private void checkTotality() throws ModelCheckException {
        symbolTypeTable.forEach((symbol, type) -> {
            switch (type) {
                case "F-0":
                    if (oneArySymbolTable.get(symbol).size() != 1) {
                        throw new ModelCheckException("The 0-ary function '" + symbol + "' must be defined. Please add it to the graph.");
                    }
                    break;
                case "F-1":
                    Set<Edge> relationSet = twoArySymbolTable.get(symbol);
                    graph.getVertexes().forEach(vertex -> {
                        if (relationSet.stream().noneMatch(edge -> edge.getFromVertex() == vertex)) {
                            throw new ModelCheckException("The 1-ary function '" + symbol + "' must be total. Please be sure that it is defined for all vertexes.");
                        }
                    });
            }
        });
    }


    /**
     * This function does the main job: the model checking. It handles the top-level type of the given formula
     * and call it self recursively on all children.<br>
     * The base case are: <br>
     * - the bind variables, which are associated with an real element throw an quantifier before<br>
     * - 0-are function symbols, which must be specified throw the graph <br>
     * Predicates and function interpretation are given throw the graph too.<br>
     * The Equality is simply implemented as the equality of two vertexes: vertexFromChild1 == vertexFromChild2 ?.
     * This is possible, because every term can be interpreted with a vertex.
     * @throws ModelCheckException if the data is invalid. This should not happen.
     */
    private boolean checkModel(FOLFormula formula) throws ModelCheckException {
        //note: this could also be done with inheritance. This would maybe the cleaner solution but I did not want to mix this could wit the datamodel.
        //Therefor I decide to make a switch case
        switch (formula.getType()) {
            case Forall:
                return graph.getVertexes().stream().allMatch(vertex -> {
                   bindVariableValues.put(formula.getChildAt(0).getName(), vertex);
                   return checkModel(formula.getChildAt(1));
                });

            case Exists:
                return graph.getVertexes().stream().anyMatch(vertex -> {
                    bindVariableValues.put(formula.getChildAt(0).getName(), vertex);
                    return checkModel(formula.getChildAt(1));
                });

            case Not:
                return !checkModel(formula.getChildAt(0));

            case And:
                return checkModel(formula.getChildAt(0)) && checkModel(formula.getChildAt(1));

            case Or:
                return checkModel(formula.getChildAt(0)) || checkModel(formula.getChildAt(1));

            case Implication:
                return !checkModel(formula.getChildAt(0)) || checkModel(formula.getChildAt(1));

            case Biimplication:
                boolean left  = checkModel(formula.getChildAt(0));
                boolean right = checkModel(formula.getChildAt(1));
                return (left && right) || (!left && !right);

            case Predicate:
                switch (formula.getChildren().size()) {
                    case 1:
                        return oneArySymbolTable.get(formula.getName()).contains(interpret(formula.getChildAt(0)));
                    case 2:
                        if (formula.getName().equals(FOLPredicate.INFIX_EQUALITY)) {
                            return interpret(formula.getChildAt(0)) == interpret(formula.getChildAt(1));
                        } else {
                            return twoArySymbolTable.get(formula.getName()).stream().anyMatch(edge ->
                             edge.getFromVertex().equals(interpret(formula.getChildAt(0))) &&
                                 edge.getToVertex().equals(interpret(formula.getChildAt(1)))
                            );
                        }
                    default:
                        throw new ModelCheckException("[ModelChecker][Internal error] Found predicate with to many children.");
                }

            case Constant:
                 return FOLConstant.TT.equals(formula.getName());

            default:
                throw new ModelCheckException("[ModelChecker][Internal error] Unknown FOLFormula-Type: " + formula.getType());
        }
    }

    /**
     * Takes a FOLFunction or FOLVariable and return the associated vertex within this interpretation.
     * @param symbol mus be a FOLFunction or an FOLVariable
     * @return a vertex which is associated with this term.
     * @throws ModelCheckException if the data is invalid. This should not happen.
     */
    private Vertex interpret(FOLFormula symbol) throws ModelCheckException {
        if (symbol instanceof FOLFunction) {
            if (symbol.getChildren().size() == 0) {
                return oneArySymbolTable.get(symbol.getName()).stream().findAny().get();
            } else if (symbol.getChildren().size() == 1) {
                Vertex childResult = interpret(symbol.getChildAt(0));
                return twoArySymbolTable.get(symbol.getName()).stream()
                    .filter(edge -> edge.getFromVertex() == childResult)
                    .findAny().get().getToVertex();
            } else {
                throw new ModelCheckException("[ModelChecker][Internal error] Found function with to many children.");
            }

        } else if (symbol instanceof FOLBoundVariable) {
            if(bindVariableValues.get(symbol.getName()) == null) {
                throw new ModelCheckException("[ModelChecker][Internal error] No bind value found for variable.");
            }
            return bindVariableValues.get(symbol.getName());
        } else {
            throw new ModelCheckException("[ModelChecker][Internal error] Not a valid function or a variable.");
        }
    }


    ////////////////////////// helping stuff ///////////////////////////////



    /**
     * A custom exception to handle error cases happened in the model check.
     */
    public static class ModelCheckException extends RuntimeException{
        /**
         * Constructs a new exception with the specified detail message.
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public ModelCheckException(String message) {
            super(message);
        }
    }
}
