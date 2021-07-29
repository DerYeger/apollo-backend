package eu.yeger.apollo.model_checker.service

import eu.yeger.apollo.model_checker.model.api.ModelCheckerRequest
import eu.yeger.apollo.model_checker.model.api.ModelCheckerResponse
import eu.yeger.apollo.shared.model.api.ApiResult

/**
 * Service for ModelChecking.
 *
 * @author Jan Müller
 */
public interface ModelCheckerService {

  /**
   * Checks if a graph is a model of a formula.
   * Validates the input and transforms the feedback as requested.
   *
   * @param modelCheckerRequest The request data, containing the selected language, graph, formula and desired feedback.
   * @return The [ApiResult] of the ModelChecking request.
   */
  public fun checkModel(modelCheckerRequest: ModelCheckerRequest): ApiResult<ModelCheckerResponse>
}
