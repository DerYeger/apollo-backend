package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.invalidated
import eu.yeger.gramofo.fol.validated
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class Operator(name: String) : Formula(name) {

    sealed class Unary(name: String, val operand: Formula) : Operator(name) {
        class Not(operand: Formula) : Unary(NOT, operand) {
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

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return "$name${operand.toString(variableAssignments, true)}"
        }
    }

    sealed class Binary(name: String, val firstOperand: Formula, val secondOperand: Formula) : Operator(name) {

        class And(firstOperand: Formula, secondOperand: Formula) : Binary(AND, firstOperand, secondOperand) {
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

        class Or(firstOperand: Formula, secondOperand: Formula) : Binary(OR, firstOperand, secondOperand) {
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

        class Implication(firstOperand: Formula, secondOperand: Formula) : Binary(IMPLICATION, firstOperand, secondOperand) {
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

        class BiImplication(firstOperand: Formula, secondOperand: Formula) : Binary(BI_IMPLICATION, firstOperand, secondOperand) {
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

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            val first = firstOperand.toString(variableAssignments, true)
            val second = secondOperand.toString(variableAssignments, true)
            return "$first $name $second"
        }
    }
}
