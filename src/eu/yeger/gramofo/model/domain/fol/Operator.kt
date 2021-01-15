package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.invalidated
import eu.yeger.gramofo.fol.validated
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

/**
 * Represents an FOL operator.
 *
 * @constructor Creates an [Operator] with the given name.
 *
 * @param name The name of this operator.
 *
 * @author Jan Müller
 */
public sealed class Operator(name: String) : Formula(name) {

    /**
     * Represents an unary FOL operator.
     *
     * @property operand The operand of this unary operator.
     * @constructor Creates an [Unary] operator with the given name and operand.
     *
     * @param name The name of this unary operator.
     *
     * @author Jan Müller
     */
    public sealed class Unary(name: String, public val operand: Formula) : Operator(name) {

        /**
         * Represents the unary negation operator.
         *
         * @constructor Creates a [Negation] operator with the given operand.
         *
         * @param operand The operand of the negation.
         *
         * @author Jan Müller
         */
        public class Negation(operand: Formula) : Unary("\u00AC", operand) {

            /**
             * Checks the [operand] by inverting [shouldBeModel].
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
                val child = operand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                return when (child.isModel) {
                    true -> invalidated(TranslationDTO("api.not.invalid"), variableAssignments, shouldBeModel, child)
                    false -> validated(TranslationDTO("api.not.valid"), variableAssignments, shouldBeModel, child)
                }
            }

            /**
             * Checks the [operand] by inverting [shouldBeModel].
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
                val child = operand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                return when (child.isModel) {
                    true -> invalidated(TranslationDTO("api.not.invalid"), variableAssignments, shouldBeModel, child)
                    false -> validated(TranslationDTO("api.not.valid"), variableAssignments, shouldBeModel, child)
                }
            }
        }

        /**
         * Returns raw [String] representation of this unary operator.
         *
         * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
         * @return The raw [String] representation of this unary operator.
         */
        override fun getRawString(variableAssignments: Map<String, Node>): String {
            return "$name${operand.toString(variableAssignments, true)}"
        }
    }

    /**
     * Represents a binary FOL operator.
     *
     * @property firstOperand The first/left operand of this binary operator.
     * @property secondOperand The second/right operand of this binary operator.
     * @constructor Creates a [Binary] operator with the given name and operand.
     *
     * @param name The name of this binary operator.
     *
     * @author Jan Müller
     */
    public sealed class Binary(name: String, public val firstOperand: Formula, public val secondOperand: Formula) : Operator(name) {

        /**
         * Represents a conjunction operator.
         *
         * @constructor Creates an [And] operator with the given conjuncts.
         *
         * @param firstOperand The first conjunct.
         * @param secondOperand The second conjunct.
         *
         * @author Jan Müller
         */
        public class And(firstOperand: Formula, secondOperand: Formula) : Binary("\u2227", firstOperand, secondOperand) {

            /**
             * Checks if both conjuncts hold.
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
                val left = firstOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.and.both"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() && right.isModel.not() -> invalidated(TranslationDTO("api.and.neither"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() -> invalidated(TranslationDTO("api.and.left"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.and.right"), variableAssignments, shouldBeModel, left, right)
                }
            }

            /**
             * Checks if both conjuncts hold.
             * [secondOperand] is only checked if [firstOperand] holds.
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
                val left = firstOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                val right by lazy { secondOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel) }
                return when {
                    left.isModel.not() -> invalidated(TranslationDTO("api.and.left"), variableAssignments, shouldBeModel, left)
                    right.isModel.not() -> invalidated(TranslationDTO("api.and.right"), variableAssignments, shouldBeModel, right)
                    else -> validated(TranslationDTO("api.and.both"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        /**
         * Represents a disjunction operator.
         *
         * @constructor Creates an [Or] operator with the given disjuncts.
         *
         * @param firstOperand The first disjunct.
         * @param secondOperand The second disjunct.
         *
         * @author Jan Müller
         */
        public class Or(firstOperand: Formula, secondOperand: Formula) : Binary("\u2228", firstOperand, secondOperand) {

            /**
             * Checks if either disjunct hold.
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
                val left = firstOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.or.both"), variableAssignments, shouldBeModel, left, right)
                    left.isModel -> validated(TranslationDTO("api.or.left"), variableAssignments, shouldBeModel, left, right)
                    right.isModel -> validated(TranslationDTO("api.or.right"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.or.neither"), variableAssignments, shouldBeModel, left, right)
                }
            }

            /**
             * Checks if either disjunct hold.
             * [secondOperand] is only checked if [firstOperand] does not hold.
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
                val left = firstOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                val right by lazy { secondOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel) }
                return when {
                    left.isModel -> validated(TranslationDTO("api.or.left"), variableAssignments, shouldBeModel, left)
                    right.isModel -> validated(TranslationDTO("api.or.right"), variableAssignments, shouldBeModel, right)
                    else -> invalidated(TranslationDTO("api.or.neither"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        /**
         * Represents an implication operator.
         *
         * @constructor Creates an [Implication] operator with the given antecedents and consequent.
         *
         * @param firstOperand The antecedent.
         * @param secondOperand The consequent.
         *
         * @author Jan Müller
         */
        public class Implication(firstOperand: Formula, secondOperand: Formula) : Binary("\u2192", firstOperand, secondOperand) {

            /**
             * Checks if consequent holds or antecedent does not hold.
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
                val left = firstOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                val right = secondOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    right.isModel -> validated(TranslationDTO("api.implication.right"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() -> validated(TranslationDTO("api.implication.left"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.implication.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }

            /**
             * Checks if consequent holds or antecedent does not hold.
             * [firstOperand] is only checked if [secondOperand] does not hold.
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
                val left by lazy { firstOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel.not()) }
                val right = secondOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    right.isModel -> validated(TranslationDTO("api.implication.right"), variableAssignments, shouldBeModel, right)
                    left.isModel.not() -> validated(TranslationDTO("api.implication.left"), variableAssignments, shouldBeModel, left)
                    else -> invalidated(TranslationDTO("api.implication.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        /**
         * Represents a bi-implication operator.
         *
         * @constructor Creates an [BiImplication] operator with the given antecedents and consequent.
         *
         * @param firstOperand The antecedent.
         * @param secondOperand The consequent.
         *
         * @author Jan Müller
         */
        public class BiImplication(firstOperand: Formula, secondOperand: Formula) : Binary("\u2194", firstOperand, secondOperand) {

            /**
             * Checks if antecedent holds if and only if consequent holds.
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
                val positive = fullCheckPositiveBiImplication(graph, symbolTable, variableAssignments, shouldBeModel)
                val negative by lazy { fullCheckNegativeBiImplication(graph, symbolTable, variableAssignments, shouldBeModel) }
                return when {
                    positive.isModel -> positive
                    negative.isModel -> negative
                    else -> invalidated(TranslationDTO("api.bi-implication.invalid"), variableAssignments, shouldBeModel, positive, negative)
                }
            }

            /**
             * Checks if antecedent holds if and only if consequent holds.
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
                val positive = partialCheckPositiveBiImplication(graph, symbolTable, variableAssignments, shouldBeModel)
                val negative by lazy { partialCheckNegativeBiImplication(graph, symbolTable, variableAssignments, shouldBeModel) }
                return when {
                    positive.isModel -> positive
                    negative.isModel -> negative
                    else -> invalidated(TranslationDTO("api.bi-implication.invalid"), variableAssignments, shouldBeModel, positive, negative)
                }
            }

            private fun fullCheckPositiveBiImplication(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.bi-implication.positive.valid"), variableAssignments, shouldBeModel, left, right)
                    left.isModel && right.isModel.not() -> invalidated(TranslationDTO("api.bi-implication.positive.right"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() && right.isModel -> invalidated(TranslationDTO("api.bi-implication.positive.left"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.bi-implication.positive.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }

            private fun partialCheckPositiveBiImplication(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.bi-implication.positive.valid"), variableAssignments, shouldBeModel, left, right)
                    left.isModel && right.isModel.not() -> invalidated(TranslationDTO("api.bi-implication.positive.right"), variableAssignments, shouldBeModel, right)
                    left.isModel.not() && right.isModel -> invalidated(TranslationDTO("api.bi-implication.positive.left"), variableAssignments, shouldBeModel, left)
                    else -> invalidated(TranslationDTO("api.bi-implication.positive.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }

            private fun fullCheckNegativeBiImplication(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                val right = secondOperand.fullCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                return when {
                    left.isModel.not() && right.isModel.not() -> validated(TranslationDTO("api.bi-implication.negative.valid"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() && right.isModel -> invalidated(TranslationDTO("api.bi-implication.negative.right"), variableAssignments, shouldBeModel, left, right)
                    left.isModel && right.isModel.not() -> invalidated(TranslationDTO("api.bi-implication.negative.left"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.bi-implication.negative.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }

            private fun partialCheckNegativeBiImplication(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                val right = secondOperand.partialCheck(graph, symbolTable, variableAssignments, shouldBeModel.not())
                return when {
                    left.isModel.not() && right.isModel.not() -> validated(TranslationDTO("api.bi-implication.negative.valid"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() && right.isModel -> invalidated(TranslationDTO("api.bi-implication.negative.right"), variableAssignments, shouldBeModel, right)
                    left.isModel && right.isModel.not() -> invalidated(TranslationDTO("api.bi-implication.negative.left"), variableAssignments, shouldBeModel, left)
                    else -> invalidated(TranslationDTO("api.bi-implication.negative.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        /**
         * Returns raw [String] representation of this binary operator.
         *
         * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
         * @return The raw [String] representation of this binary operator.
         */
        override fun getRawString(variableAssignments: Map<String, Node>): String {
            val first = firstOperand.toString(variableAssignments, true)
            val second = secondOperand.toString(variableAssignments, true)
            return "$first $name $second"
        }
    }
}
