package eu.yeger.gramofo.service

import com.github.michaelbull.result.*
import eu.yeger.gramofo.fol.English
import eu.yeger.gramofo.fol.German
import eu.yeger.gramofo.fol.Language
import eu.yeger.gramofo.fol.checkModel
import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.model.api.*
import eu.yeger.gramofo.model.dto.TranslationDTO

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ApiResult<ModelCheckerResponse> = binding {
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
            .mapError { translationDTO -> HttpEntity.UnprocessableEntity(translationDTO) }
            .bind()
        extractLanguage(modelCheckerRequest)
            .andThen { language -> parseFormula(modelCheckerRequest.formula, language) }
            .mapError { error -> TranslationDTO(error) }
            .andThen { formula -> checkModel(domainGraph, formula, modelCheckerRequest.minimizeResult) }
            .mapError { translationDTO -> HttpEntity.UnprocessableEntity(translationDTO) }
            .map { trace -> HttpEntity.Ok(ModelCheckerResponse(trace, modelCheckerRequest.minimizeResult)) }
            .bind()
    }

    private fun extractLanguage(modelCheckerRequest: ModelCheckerRequest): Result<Language, String> {
        return when (modelCheckerRequest.language) {
            "en" -> Ok(English)
            "de" -> Ok(German)
            else -> return Err("Unsupported language \"${modelCheckerRequest.language}\".")
        }
    }
}
