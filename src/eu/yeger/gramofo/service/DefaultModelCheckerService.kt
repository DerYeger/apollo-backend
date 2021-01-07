package eu.yeger.gramofo.service

import com.github.michaelbull.result.*
import eu.yeger.gramofo.fol.English
import eu.yeger.gramofo.fol.German
import eu.yeger.gramofo.fol.Language
import eu.yeger.gramofo.fol.checkModel
import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.model.api.ApiResult
import eu.yeger.gramofo.model.api.HttpEntity
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.toDomainModel
import eu.yeger.gramofo.model.domain.fol.ModelCheckerTrace
import eu.yeger.gramofo.model.dto.TranslationDTO

class DefaultModelCheckerService : ModelCheckerService {

    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ApiResult<ModelCheckerTrace> = binding {
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
            .mapError { translationDTO -> HttpEntity.UnprocessableEntity(translationDTO) }
            .bind()
        extractLanguage(modelCheckerRequest)
            .andThen { language -> parseFormula(modelCheckerRequest.formula, language) }
            .mapError { error -> TranslationDTO(error) }
            .andThen { formula -> checkModel(domainGraph, formula) }
            .mapError { translationDTO -> HttpEntity.UnprocessableEntity(translationDTO) }
            .map { modelCheckerTrace -> HttpEntity.Ok(modelCheckerTrace) }
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
