package eu.yeger.apollo.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the feedback-selection of a ModelChecking request.
 *
 * @author Jan Müller
 */
@Serializable
public enum class Feedback {
    /**
     * Indicates that redundant checks are evaluated.
     */
    @SerialName("full") Full,
    /**
     * Indicates that only required checks are evaluated.
     */
    @SerialName("relevant") Relevant,
    /**
     * Indicates that only required checks are evaluated and only the result is returned.
     */
    @SerialName("minimal") Minimal,
}
