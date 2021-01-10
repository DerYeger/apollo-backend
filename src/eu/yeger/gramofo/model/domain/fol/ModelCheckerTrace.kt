package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.dto.TranslationDTO
import kotlinx.serialization.Serializable

@Serializable
data class ModelCheckerTrace(
    val formula: String,
    val description: TranslationDTO,
    val isModel: Boolean,
    val shouldBeModel: Boolean,
    val children: List<ModelCheckerTrace>? = null,
)
