package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.invalidated
import eu.yeger.gramofo.fol.validated
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

sealed class Constant(name: String) : Formula(name = name) {

    class True : Constant(TT)
    class False : Constant(FF)

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
