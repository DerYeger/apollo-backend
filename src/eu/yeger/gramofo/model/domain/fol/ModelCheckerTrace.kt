package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.dto.TranslationDTO
import kotlinx.serialization.Serializable

/**
 * Trace of the ModelChecking algorithm.
 * Contains information about a single check and its child checks.
 *
 * @property formula The [String] representation of the checked [Formula].
 * @property description [TranslationDTO] with a description key for this check.
 * @property isModel Indicates that the [Graph] is a model of the checked [formula].
 * @property shouldBeModel Indicates that the [Graph] is supposed to be model of checked [formula].
 * @property children [List] of child-traces.
 * @constructor Creates a [ModelCheckerTrace] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
public data class ModelCheckerTrace(
    val formula: String,
    val description: TranslationDTO,
    val isModel: Boolean,
    val shouldBeModel: Boolean,
    val children: List<ModelCheckerTrace>? = null,
)
