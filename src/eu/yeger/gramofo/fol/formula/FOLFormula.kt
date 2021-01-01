package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

abstract class FOLFormula(override val name: String) : FOLEntity {
    override var hasBrackets: Boolean = false
    override var hasDot: Boolean = false

    abstract fun checkModel(graph: Graph, symbolTable: SymbolTable, variableAssignments: Map<String, Node>, shouldBeModel: Boolean): ModelCheckerTrace

    abstract override fun getFormulaString(variableAssignments: Map<String, Node>): String

    final override fun toString(): String {
        return toString(emptyMap())
    }

    companion object {
        const val TT = "tt"
        const val FF = "ff"
        const val NOT = "\u00AC"
        const val AND = "\u2227"
        const val OR = "\u2228"
        const val IMPLICATION = "\u2192"
        const val BI_IMPLICATION = "\u2194"
        const val EXISTS = "\u2203"
        const val FOR_ALL = "\u2200"
        const val INFIX_EQUALITY = "=" // equal sign with a dot on top
    }

    protected fun validated(description: TranslationDTO, variableAssignments: Map<String, Node>, shouldBeModel: Boolean, vararg children: ModelCheckerTrace) =
        ModelCheckerTrace(
            formula = this.toString(variableAssignments),
            description = description,
            isModel = true,
            shouldBeModel = shouldBeModel,
            children = children.toList()
        )

    protected fun invalidated(description: TranslationDTO, variableAssignments: Map<String, Node>, shouldBeModel: Boolean, vararg children: ModelCheckerTrace) =
        ModelCheckerTrace(
            formula = this.toString(variableAssignments),
            description = description,
            isModel = false,
            shouldBeModel = shouldBeModel,
            children = children.toList()
        )
}
