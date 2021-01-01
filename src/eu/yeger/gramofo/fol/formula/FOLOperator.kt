package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class FOLOperator(name: String) : FOLFormula(name) {

    sealed class Unary(name: String, val operand: FOLFormula) : FOLOperator(name) {
        class Not(operand: FOLFormula) : Unary(NOT, operand) {
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

    sealed class Binary(name: String, val firstOperand: FOLFormula, val secondOperand: FOLFormula) : FOLOperator(name) {

        class And(firstOperand: FOLFormula, secondOperand: FOLFormula) : Binary(AND, firstOperand, secondOperand) {
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

        class Or(firstOperand: FOLFormula, secondOperand: FOLFormula) : Binary(OR, firstOperand, secondOperand) {
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

        class Implication(firstOperand: FOLFormula, secondOperand: FOLFormula) : Binary(IMPLICATION, firstOperand, secondOperand) {
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

        class BiImplication(firstOperand: FOLFormula, secondOperand: FOLFormula) : Binary(BI_IMPLICATION, firstOperand, secondOperand) {
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
