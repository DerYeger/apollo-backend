package eu.yeger.apollo.service

import com.github.michaelbull.result.*
import eu.yeger.apollo.fol.checkModel
import eu.yeger.apollo.fol.parser.English
import eu.yeger.apollo.fol.parser.German
import eu.yeger.apollo.fol.parser.Language
import eu.yeger.apollo.fol.parser.parseFormula
import eu.yeger.apollo.model.api.*
import eu.yeger.apollo.model.dto.TranslationDTO

/**
 * Service for ModelChecking.
 *
 * @constructor Creates a [DefaultModelCheckerService].
 *
 * @author Jan Müller
 */
public class DefaultModelCheckerService : ModelCheckerService {

  /**
   * Checks if a graph is a model of a formula.
   * Validates the input and transforms the feedback as requested.
   *
   * @param modelCheckerRequest The request data, containing the selected language, graph, formula and desired feedback.
   * @return The [ApiResult] of the ModelChecking request.
   */
  override fun checkModel(modelCheckerRequest: ModelCheckerRequest): ApiResult<ModelCheckerResponse> = binding {
    val domainGraph = modelCheckerRequest.graph.toDomainModel()
      .mapError { translationDTO -> HttpResponseEntity.unprocessableEntity(translationDTO) }
      .bind()
    extractLanguage(modelCheckerRequest)
      .andThen { language -> parseFormula(modelCheckerRequest.formula, language) }
      .mapError { error -> TranslationDTO(error) }
      .andThen { formula -> checkModel(domainGraph, formula, modelCheckerRequest.feedback) }
      .mapError { translationDTO -> HttpResponseEntity.unprocessableEntity(translationDTO) }
      .map { trace -> HttpResponseEntity.ok(ModelCheckerResponse(trace, modelCheckerRequest.feedback)) }
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
