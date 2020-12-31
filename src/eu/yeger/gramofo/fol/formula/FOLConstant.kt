package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class FOLConstant(name: String) : FOLFormula(name = name) {
    class True : FOLConstant(TT)
    class False : FOLConstant(FF)

    override fun checkModel(
        graph: Graph,
        symbolTable: SymbolTable,
        variableAssignments: Map<String, Node>,
        shouldBeModel: Boolean,
    ): ModelCheckerTrace {
        return when (this) {
            is True -> validated(TranslationDTO("api.constant.true"), variableAssignments, shouldBeModel)
            is False -> invalidated(TranslationDTO("api.constant.false"), variableAssignments, shouldBeModel)
        }
    }

    override fun getFormulaString(variableAssignments: Map<String, Node>): String {
        return name
    }
}
