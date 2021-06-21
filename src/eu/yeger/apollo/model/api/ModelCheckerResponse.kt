package eu.yeger.apollo.model.api

import eu.yeger.apollo.model.domain.fol.ModelCheckerTrace
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
public data class ModelCheckerResponse(
  val rootTrace: ModelCheckerTrace,
  val feedback: Feedback,
)
