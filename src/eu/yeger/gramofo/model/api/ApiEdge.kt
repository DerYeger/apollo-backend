package eu.yeger.gramofo.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiEdge(
    val source: String,
    val target: String,
    val relations: List<String>,
    val functions: List<String>
)
