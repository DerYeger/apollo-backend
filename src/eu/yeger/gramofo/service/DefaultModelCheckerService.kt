package eu.yeger.gramofo.service

import eu.yeger.gramofo.fol.FOLParser
import eu.yeger.gramofo.fol.graph.ModelChecker
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.toDomainModel

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): Boolean {
        val parsedFormula = FOLParser().parseFormula(modelCheckerRequest.formula).result ?: return false
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
        return ModelChecker().checkModel(domainGraph, parsedFormula)
    }
}
