package eu.yeger.gramofo.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import eu.yeger.gramofo.fol.English
import eu.yeger.gramofo.fol.German
import eu.yeger.gramofo.fol.Language
import eu.yeger.gramofo.fol.graph.ModelCheckerResult
import eu.yeger.gramofo.fol.graph.checkModel
import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.toDomainModel
import java.util.*

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ModelCheckerResult {
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
        return extractLanguage(modelCheckerRequest)
            .andThen { language -> parseFormula(modelCheckerRequest.formula, language) }
            .andThen { formula -> checkModel(domainGraph, formula) }
    }

    private fun extractLanguage(modelCheckerRequest: ModelCheckerRequest): Result<Language, String> {
        return when (modelCheckerRequest.language) {
            "en" -> Ok(English)
            "de" -> Ok(German)
            else -> return Err("Unsupported language \"${modelCheckerRequest.language}\".")
        }
    }
}
