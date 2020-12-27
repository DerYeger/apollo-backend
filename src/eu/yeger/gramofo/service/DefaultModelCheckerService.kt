package eu.yeger.gramofo.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import eu.yeger.gramofo.fol.graph.ModelCheckerTrace
import eu.yeger.gramofo.fol.graph.checkModel
import eu.yeger.gramofo.fol.parseFormula
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.ModelCheckerResponse
import eu.yeger.gramofo.model.api.toDomainModel
import java.util.*

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ModelCheckerResponse {
        val locale = when (modelCheckerRequest.language) {
            "en" -> Locale.ENGLISH
            "de" -> Locale.GERMAN
            else -> return ModelCheckerResponse(error = "Unsupported language \"${modelCheckerRequest.language}\".")
        }
        val parseResult = parseFormula(modelCheckerRequest.formula, locale)
        val parsedFormula =
            parseResult.result ?: return ModelCheckerResponse(error = parseResult.errorMessage ?: "Error")
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
        return return when (val checkResult = checkModel(domainGraph, parsedFormula)) {
            is Ok<ModelCheckerTrace> -> ModelCheckerResponse(result = checkResult.value)
            is Err<String> -> ModelCheckerResponse(error = checkResult.error)
        }
    }
}
