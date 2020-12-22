package eu.yeger.gramofo.service

import eu.yeger.gramofo.fol.graph.checkModel
import eu.yeger.gramofo.fol.parseFormula
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.ModelCheckerResponse
import eu.yeger.gramofo.model.api.toDomainModel
import java.util.*

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(request: ModelCheckerRequest): ModelCheckerResponse {
        val locale = when (request.language) {
            "en" -> Locale.ENGLISH
            "de" -> Locale.GERMAN
            else -> return ModelCheckerResponse("Unsupported language \"${request.language}\".")
        }
        val parseResult = parseFormula(request.formula, locale)
        val parsedFormula = parseResult.result ?: return ModelCheckerResponse(parseResult.errorMessage ?: "Error")
        val domainGraph = request.graph.toDomainModel()
        val checkResult = checkModel(domainGraph, parsedFormula)
        return ModelCheckerResponse(checkResult)
    }
}
