package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class Quantifier(
    name: String,
    val variable: BoundVariable,
    val operand: Formula,
) : Formula(name) {

    class Existential(variable: BoundVariable, operand: Formula) : Quantifier(EXISTS, variable, operand) {
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

    class Universal(variable: BoundVariable, operand: Formula) : Quantifier(FOR_ALL, variable, operand) {
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

    private fun isUnary(formula: Formula): Boolean {
        return when (formula) {
            is Existential -> true
            is Universal -> true
            is Operator.Unary.Not -> true
            else -> true
        }
    }
}
