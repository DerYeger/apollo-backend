package eu.yeger.gramofo.model.api

import kotlinx.serialization.*

@Serializable
data class ModelCheckerRequest(
    val formula: String,
    val graph: ApiGraph,
    val language: String,
    val minimizeResult: Boolean,
)
