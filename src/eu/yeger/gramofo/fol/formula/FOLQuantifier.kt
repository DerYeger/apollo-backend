package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class FOLQuantifier(
    name: String,
    variable: FOLBoundVariable,
    operand: FOLFormula,
) : FOLFormula(name, setOf(variable, operand)) {

    class Existential(variable: FOLBoundVariable, operand: FOLFormula) : FOLQuantifier(EXISTS, variable, operand) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val variableName = getChildAt(0).name
            val children = graph.nodes.map { node: Node ->
                getChildAt(1).checkModel(graph, symbolTable, variableAssignments + (variableName to node), shouldBeModel)
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
            val variableName = getChildAt(0).name
            val children = graph.nodes.map { node: Node ->
                getChildAt(1).checkModel(graph, symbolTable, variableAssignments + (variableName to node), shouldBeModel)
            }
            return if (children.all(ModelCheckerTrace::isModel)) {
                validated(TranslationDTO("api.forall.valid"), variableAssignments, shouldBeModel, *children.toTypedArray())
            } else {
                invalidated(TranslationDTO("api.forall.invalid"), variableAssignments, shouldBeModel, *children.toTypedArray())
            }
        }
    }

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        val child0 = getChildAt(0)
        val child1 = getChildAt(1)
        val sb = StringBuilder()
        sb.append(name)
        sb.append(child0.getFormulaString(variableAssignments))
        if (!child1.hasDot && !isUnary(child1)) {
            sb.append(" ")
        }
        sb.append(child1.getFormulaString(variableAssignments))
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    private fun isUnary(formula: FOLFormula): Boolean {
        return when (formula) {
            is Existential -> true
            is Universal -> true
            is FOLOperator.Not -> true
            else -> true
        }
    }
}
