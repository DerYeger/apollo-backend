package eu.yeger.gramofo.service

import com.github.michaelbull.result.*
import eu.yeger.gramofo.fol.English
import eu.yeger.gramofo.fol.German
import eu.yeger.gramofo.fol.Language
import eu.yeger.gramofo.fol.checkModel
import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.model.api.*
import eu.yeger.gramofo.model.dto.TranslationDTO

/**
 * Service for ModelChecking.
 *
 * @constructor Creates a [DefaultModelCheckerService].
 *
 * @author Jan Müller
 */
class DefaultModelCheckerService : ModelCheckerService {

    /**
     * Checks if a graph is a model of a formula.
     * Validates the input and transforms the feedback as requested.
     *
     * @param modelCheckerRequest The request data, containing the selected language, graph, formula and desired feedback.
     * @return The [ApiResult] of the ModelChecking request.
     */
    override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ApiResult<ModelCheckerResponse> = binding {
        val domainGraph = modelCheckerRequest.graph.toDomainModel()
            .mapError { translationDTO -> HttpEntity.UnprocessableEntity(translationDTO) }
            .bind()
        extractLanguage(modelCheckerRequest)
            .andThen { language -> parseFormula(modelCheckerRequest.formula, language) }
            .mapError { error -> TranslationDTO(error) }
            .andThen { formula -> checkModel(domainGraph, formula, modelCheckerRequest.feedback) }
            .mapError { translationDTO -> HttpEntity.UnprocessableEntity(translationDTO) }
            .map { trace -> HttpEntity.Ok(ModelCheckerResponse(trace, modelCheckerRequest.feedback)) }
            .bind()
    }

    /**
     * Returns the selected [Language] from a [ModelCheckerRequest] or an error if the [Language] is not supported.
     *
     * @param modelCheckerRequest The source [ModelCheckerRequest].
     * @return A [Result] containing the [Language] or an untranslated error message.
     */
    private fun extractLanguage(modelCheckerRequest: ModelCheckerRequest): Result<Language, String> {
        return when (modelCheckerRequest.language) {
            "en" -> Ok(English)
            "de" -> Ok(German)
            else -> return Err("Unsupported language \"${modelCheckerRequest.language}\".")
        }
    }
}
