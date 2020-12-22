package eu.yeger.gramofo.service

import eu.yeger.gramofo.fol.graph.checkModel
import eu.yeger.gramofo.fol.parseFormula
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.ModelCheckerResponse
import eu.yeger.gramofo.model.api.toDomainModel

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ModelCheckerResponse {
        val parseResult = parseFormula(modelCheckerRequest.formula)
        val parsedFormula = parseResult.result ?: return ModelCheckerResponse(parseResult.errorMessage ?: "Error")
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
        val checkResult = checkModel(domainGraph, parsedFormula)
        return ModelCheckerResponse(checkResult)
    }
}
