package eu.yeger.gramofo.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a feedback-selection of a ModelChecking request.
 * [Full] indicates that redundant checks are evaluated.
 * [Relevant] indicates that only required checks are evaluated.
 * [Minimal] indicates that only required checks are evaluated and only the result is returned.
 *
 * @author Jan Müller
 */
@Serializable
enum class Feedback {
    @SerialName("full") Full,
    @SerialName("relevant") Relevant,
    @SerialName("minimal") Minimal
}
