package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class FOLQuantifier(
    name: String,
    val variable: FOLBoundVariable,
    val operand: FOLFormula,
) : FOLFormula(name) {

    class Existential(variable: FOLBoundVariable, operand: FOLFormula) : FOLQuantifier(EXISTS, variable, operand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val children = graph.nodes.map { node: Node ->
                operand.checkModel(graph, symbolTable, variableAssignments + (variable.name to node), shouldBeModel)
            }
            return if (children.any(ModelCheckerTrace::isModel)) {
                validated(TranslationDTO("api.exists.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
            } else {
                invalidated(TranslationDTO("api.exists.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
            }
        }
    }

    class Universal(variable: FOLBoundVariable, operand: FOLFormula) : FOLQuantifier(FOR_ALL, variable, operand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val children = graph.nodes.map { node: Node ->
                operand.checkModel(graph, symbolTable, variableAssignments + (variable.name to node), shouldBeModel)
            }
            return if (children.all(ModelCheckerTrace::isModel)) {
                validated(TranslationDTO("api.forall.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
            } else {
                invalidated(TranslationDTO("api.forall.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
            }
        }
    }

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return buildString {
            append(name)
            append(variable.getFormulaString(variableAssignments))
            if (!operand.hasDot && !isUnary(operand)) {
                append(" ")
            }
            append(operand.getFormulaString(variableAssignments))
            maybeWrapBracketsAndDot()
        }
    }

    private fun isUnary(formula: FOLFormula): Boolean {
        return when (formula) {
            is Existential -> true
            is Universal -> true
            is FOLOperator.Unary.Not -> true
            else -> true
        }
    }
}
