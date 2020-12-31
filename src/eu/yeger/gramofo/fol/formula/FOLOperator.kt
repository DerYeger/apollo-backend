package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class FOLOperator : FOLFormula {
    constructor(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula
    ) : super(name, setOf(leftOperand, rightOperand))

    constructor(
        name: String,
        operand: FOLFormula,
    ) : super(name, setOf(operand))

    class Not(operand: FOLFormula) : FOLOperator(NOT, operand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val child = getChildAt(0).checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
            return when (child.isModel) {
                true -> invalidated(TranslationDTO("api.not.invalid"), variableAssignments, shouldBeModel, child)
                false -> validated(TranslationDTO("api.not.valid"), variableAssignments, shouldBeModel, child)
            }
        }
    }

    class And(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(AND, leftOperand, rightOperand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = getChildAt(0).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            val right = getChildAt(1).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            return when {
                left.isModel && right.isModel -> validated(TranslationDTO("api.and.both"), variableAssignments, shouldBeModel, left, right)
                left.isModel.not() && right.isModel.not() -> invalidated(TranslationDTO("api.and.neither"), variableAssignments, shouldBeModel, left, right)
                left.isModel.not() -> invalidated(TranslationDTO("api.and.left"), variableAssignments, shouldBeModel, left, right)
                else -> invalidated(TranslationDTO("api.and.right"), variableAssignments, shouldBeModel, left, right)
            }
        }
    }

    class Or(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(OR, leftOperand, rightOperand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = getChildAt(0).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            val right = getChildAt(1).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            return when {
                left.isModel && right.isModel -> validated(TranslationDTO("api.or.both"), variableAssignments, shouldBeModel, left, right)
                left.isModel -> validated(TranslationDTO("api.or.left"), variableAssignments, shouldBeModel, left, right)
                right.isModel -> validated(TranslationDTO("api.or.right"), variableAssignments, shouldBeModel, left, right)
                else -> invalidated(TranslationDTO("api.or.neither"), variableAssignments, shouldBeModel, left, right)
            }
        }
    }

    class Implication(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(IMPLICATION, leftOperand, rightOperand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = getChildAt(0).checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
            val right = getChildAt(1).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            return when {
                right.isModel -> validated(TranslationDTO("api.implication.right"), variableAssignments, shouldBeModel, left, right)
                left.isModel.not() -> validated(TranslationDTO("api.implication.left"), variableAssignments, shouldBeModel, left, right)
                else -> invalidated(TranslationDTO("api.implication.invalid"), variableAssignments, shouldBeModel, left, right)
            }
        }
    }

    class BiImplication(leftOperand: FOLFormula, rightOperand: FOLFormula) : FOLOperator(BI_IMPLICATION, leftOperand, rightOperand) {
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

        private fun FOLFormula.checkPositiveBiImplication(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = getChildAt(0).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            val right = getChildAt(1).checkModel(graph, symbolTable, variableAssignments, shouldBeModel)
            return when {
                left.isModel && right.isModel -> validated(TranslationDTO("api.bi-implication.positive.valid"), variableAssignments, shouldBeModel, left, right)
                else -> invalidated(TranslationDTO("api.bi-implication.positive.invalid"), variableAssignments, shouldBeModel, left, right)
            }
        }

        private fun FOLFormula.checkNegativeBiImplication(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = getChildAt(0).checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
            val right = getChildAt(1).checkModel(graph, symbolTable, variableAssignments, shouldBeModel.not())
            return when {
                left.isModel.not() && right.isModel.not() -> validated(TranslationDTO("api.bi-implication.negative.valid"), variableAssignments, shouldBeModel, left, right)
                else -> invalidated(TranslationDTO("api.bi-implication.negative.invalid"), variableAssignments, shouldBeModel, left, right)
            }
        }
    }

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        val sb = StringBuilder()
        if (name == NOT) {
            sb.append(name)
            sb.append(getChildAt(0).getFormulaString(variableAssignments))
        } else {
            sb.append(getChildAt(0).getFormulaString(variableAssignments))
            sb.append(" ")
            sb.append(name)
            sb.append(" ")
            sb.append(getChildAt(1).getFormulaString(variableAssignments))
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }
}
