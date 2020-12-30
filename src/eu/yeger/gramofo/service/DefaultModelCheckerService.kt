package eu.yeger.gramofo.service

import com.github.michaelbull.result.Err
import eu.yeger.gramofo.fol.graph.ModelCheckerResult
import eu.yeger.gramofo.fol.graph.checkModel
import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.toDomainModel
import java.util.*

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ModelCheckerResult {
        val locale = when (modelCheckerRequest.language) {
            "en" -> Locale.ENGLISH
            "de" -> Locale.GERMAN
            else -> return Err("Unsupported language \"${modelCheckerRequest.language}\".")
        }
        val parseResult = parseFormula(modelCheckerRequest.formula, locale)
        val parsedFormula =
            parseResult.result ?: return Err(parseResult.errorMessage ?: "Error")
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
        return checkModel(domainGraph, parsedFormula)
    }
}
