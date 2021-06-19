package eu.yeger.apollo.service

import eu.yeger.apollo.model.api.ApiResult
import eu.yeger.apollo.model.api.ModelCheckerRequest
import eu.yeger.apollo.model.api.ModelCheckerResponse

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
