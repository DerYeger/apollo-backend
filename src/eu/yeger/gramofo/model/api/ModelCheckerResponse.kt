package eu.yeger.gramofo.model.api

import eu.yeger.gramofo.model.domain.fol.ModelCheckerTrace
import kotlinx.serialization.Serializable

/**
 * Response of the ModelChecking algorithm. Contains the result (and it's children) and the [Feedback]-selection.
 *
 * @property rootTrace The root [ModelCheckerTrace], that contains the results of the algorithm.
 * @property feedback The [Feedback]-selection.
 * @constructor Creates a [ModelCheckerResponse] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
data class ModelCheckerResponse(
    val rootTrace: ModelCheckerTrace,
    val feedback: Feedback,
)
