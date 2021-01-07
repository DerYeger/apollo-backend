package eu.yeger.gramofo.model.api

import eu.yeger.gramofo.model.domain.fol.ModelCheckerTrace
import kotlinx.serialization.Serializable

@Serializable
data class ModelCheckerResponse(
    val rootTrace: ModelCheckerTrace,
    val isMinimized: Boolean,
)
