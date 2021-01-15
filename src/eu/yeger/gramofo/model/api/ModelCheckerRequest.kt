package eu.yeger.gramofo.model.api

import kotlinx.serialization.*

/**
 * Request of the ModelChecking algorithm. Contains the necessary data to execute the algorithm.
 *
 * @property formula The formula.
 * @property graph The [ApiGraph] structure.
 * @property language The language that will be used for error messages of the formula-parser.
 * @property feedback The requested [Feedback]-selection.
 * @constructor Creates a [ModelCheckerRequest] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
public data class ModelCheckerRequest(
    val formula: String,
    val graph: ApiGraph,
    val language: String,
    val feedback: Feedback,
)
