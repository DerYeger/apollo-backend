package eu.yeger.apollo.model.domain.fol

import eu.yeger.apollo.fol.invalidated
import eu.yeger.apollo.fol.validated
import eu.yeger.apollo.model.domain.Edge
import eu.yeger.apollo.model.domain.Graph
import eu.yeger.apollo.model.domain.Node
import eu.yeger.apollo.model.dto.TranslationDTO

private val specialNames = mapOf(
  "=" to "\u2250",
  "<=" to "\u2264",
  ">=" to "\u2265"
)

/**
 * Represents an FOL relation.
 *
 * @constructor Creates a [Relation] with the given name.
 *
 * @param name The name of this relation.
 *
 * @author Jan Müller
 */
public sealed class Relation(name: String) : Formula(name) {

  /**
   * Represents an unary FOL relation.
   *
   * @property term The [Term] of this unary relation.
   * @constructor Creates an [Unary] relation with the given name.
   *
   * @param name The name of this unary relation.
   *
   * @author Jan Müller
   */
  public class Unary(name: String, public val term: Term) : Relation(name) {

    /**
     * Checks if the [Node] which [term] evaluates to is part of this [Relation].
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun fullCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      val node = term.evaluate(symbolTable, variableAssignments)
      val translationParams = mapOf("relation" to name, "node" to node.name)
      return when (symbolTable.unarySymbols[name]!!.contains(node)) {
        true -> validated(
          TranslationDTO("api.relation.unary.valid", translationParams),
          variableAssignments,
          shouldBeModel
        )
        false -> invalidated(
          TranslationDTO("api.relation.unary.invalid", translationParams),
          variableAssignments,
          shouldBeModel
        )
      }
    }

    /**
     * Checks if the [Node] which [term] evaluates to is part of this [Relation].
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun partialCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      return fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
    }

    /**
     * Returns raw [String] representation of unary relation.
     *
     * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
     * @return The raw [String] representation of this unary relation.
     */
    override fun getRawString(variableAssignments: Map<String, Node>): String {
      val relation = specialNames.getOrDefault(name, name)
      val termString = term.toString(variableAssignments, true)
      return "$relation($termString)"
    }
  }

  /**
   * Represents a binary FOL relation.
   *
   * @property firstTerm The first/left [Term] of this binary relation.
   * @property secondTerm The second/right [Term] of this binary relation.
   * @property isInfix Indicates that this binary relation has infix-notation.
   * @constructor Creates an [Binary] relation with the given name.
   *
   * @param name The name of this binary relation.
   *
   * @author Jan Müller
   */
  public class Binary(
    name: String,
    public val firstTerm: Term,
    public val secondTerm: Term,
    public val isInfix: Boolean,
  ) : Relation(name) {

    /**
     * Checks if the [Node]s which [firstTerm] and [secondTerm] evaluate to are part of this [Relation].
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun fullCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      val left = firstTerm.evaluate(symbolTable, variableAssignments)
      val right = secondTerm.evaluate(symbolTable, variableAssignments)
      val translationParams = mapOf(
        "firstTerm" to firstTerm.toString(variableAssignments, false),
        "secondTerm" to secondTerm.toString(variableAssignments, false),
        "firstResult" to left.name,
        "secondResult" to right.name
      )
      return when (name) {
        "=" -> checkEquality(left, right, variableAssignments, shouldBeModel, translationParams)
        else -> checkRegularRelation(left, right, symbolTable, variableAssignments, shouldBeModel, translationParams)
      }
    }

    private fun checkEquality(
      left: Node,
      right: Node,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
      translationParams: Map<String, String>,
    ): ModelCheckerTrace {
      return when (left) {
        right -> validated(
          TranslationDTO("api.relation.equality.valid", translationParams),
          variableAssignments,
          shouldBeModel
        )
        else -> invalidated(
          TranslationDTO("api.relation.equality.invalid", translationParams),
          variableAssignments,
          shouldBeModel
        )
      }
    }

    private fun checkRegularRelation(
      left: Node,
      right: Node,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
      translationParams: Map<String, String>,
    ): ModelCheckerTrace {
      val binaryTranslationParams = translationParams + ("relation" to name)
      return when (symbolTable.binarySymbols[name]!!.any { edge: Edge -> edge.source == left && edge.target == right }) {
        true -> validated(
          TranslationDTO("api.relation.binary.valid", binaryTranslationParams),
          variableAssignments,
          shouldBeModel
        )
        else -> invalidated(
          TranslationDTO("api.relation.binary.invalid", binaryTranslationParams),
          variableAssignments,
          shouldBeModel
        )
      }
    }

    /**
     * Checks if the [Node]s which [firstTerm] and [secondTerm] evaluate to are part of this [Relation].
     *
     * @param graph The [Graph] that will be checked.
     * @param symbolTable [SymbolTable] that contains all symbols of the parsed root formula and [graph].
     * @param variableAssignments [Map] of [BoundVariable] names and their assigned [Node]s.
     * @param shouldBeModel Indicates the expected result. Can be false for subformulas of [Operator.Unary.Negation].
     * @return [ModelCheckerTrace] that contains the results of this check.
     */
    override fun partialCheck(
      graph: Graph,
      symbolTable: SymbolTable,
      variableAssignments: Map<String, Node>,
      shouldBeModel: Boolean,
    ): ModelCheckerTrace {
      return fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
    }

    /**
     * Returns raw [String] representation of binary relation.
     *
     * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
     * @return The raw [String] representation of this binary relation.
     */
    override fun getRawString(variableAssignments: Map<String, Node>): String {
      val relation = specialNames.getOrDefault(name, name)
      val first = firstTerm.toString(variableAssignments, true)
      val second = secondTerm.toString(variableAssignments, true)
      return when (isInfix) {
        true -> "$first $relation $second"
        false -> "$relation($first, $second)"
      }
    }
  }
}
