package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class Operator(name: String) : Formula(name) {

    sealed class Unary(name: String, val operand: Formula) : Operator(name) {
        class Not(operand: Formula) : Unary(NOT, operand) {
            override fun checkModel(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val child = operand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
                return when (child.isModel) {
                    true -> invalidated(TranslationDTO("api.not.invalid"), variableAssignments, shouldBeModel, child)
                    false -> validated(TranslationDTO("api.not.valid"), variableAssignments, shouldBeModel, child)
                }
            }
        }

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return buildString {
                append(name)
                append(operand.getFormulaString(variableAssignments))
                maybeWrapBracketsAndDot()
            }
        }
    }

    sealed class Binary(name: String, val firstOperand: Formula, val secondOperand: Formula) : Operator(name) {

        class And(firstOperand: Formula, secondOperand: Formula) : Binary(AND, firstOperand, secondOperand) {
            override fun checkModel(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.and.both"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() && right.isModel.not() -> invalidated(TranslationDTO("api.and.neither"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() -> invalidated(TranslationDTO("api.and.left"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.and.right"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        class Or(firstOperand: Formula, secondOperand: Formula) : Binary(OR, firstOperand, secondOperand) {
            override fun checkModel(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.or.both"), variableAssignments, shouldBeModel, left, right)
                    left.isModel -> validated(TranslationDTO("api.or.left"), variableAssignments, shouldBeModel, left, right)
                    right.isModel -> validated(TranslationDTO("api.or.right"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.or.neither"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        class Implication(firstOperand: Formula, secondOperand: Formula) : Binary(IMPLICATION, firstOperand, secondOperand) {
            override fun checkModel(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
                val right = secondOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    right.isModel -> validated(TranslationDTO("api.implication.right"), variableAssignments, shouldBeModel, left, right)
                    left.isModel.not() -> validated(TranslationDTO("api.implication.left"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.implication.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        class BiImplication(firstOperand: Formula, secondOperand: Formula) : Binary(BI_IMPLICATION, firstOperand, secondOperand) {
            override fun checkModel(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val positive = checkPositiveBiImplication(graph, symbolTable, variableAssignments, shouldBeModel)
                val negative by lazy { checkNegativeBiImplication(graph, symbolTable, variableAssignments, shouldBeModel) }
                return when {
                    positive.isModel -> positive
                    negative.isModel -> negative
                    else -> invalidated(TranslationDTO("api.bi-implication.invalid"), variableAssignments, shouldBeModel, positive, negative)
                }
            }

            private fun checkPositiveBiImplication(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                val right = secondOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
                return when {
                    left.isModel && right.isModel -> validated(TranslationDTO("api.bi-implication.positive.valid"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.bi-implication.positive.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }

            private fun checkNegativeBiImplication(
                graph: Graph,
                symbolTable: SymbolTable,
                variableAssignments: Map<String, Node>,
                shouldBeModel: Boolean,
            ): ModelCheckerTrace {
                val left = firstOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
                val right = secondOperand.checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
                return when {
                    left.isModel.not() && right.isModel.not() -> validated(TranslationDTO("api.bi-implication.negative.valid"), variableAssignments, shouldBeModel, left, right)
                    else -> invalidated(TranslationDTO("api.bi-implication.negative.invalid"), variableAssignments, shouldBeModel, left, right)
                }
            }
        }

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return buildString {
                append(firstOperand.getFormulaString(variableAssignments))
                append(" ")
                append(name)
                append(" ")
                append(secondOperand.getFormulaString(variableAssignments))
                maybeWrapBracketsAndDot()
            }
        }
    }
}
