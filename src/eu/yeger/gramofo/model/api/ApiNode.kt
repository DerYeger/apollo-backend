package eu.yeger.gramofo.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiNode(
    val name: String,
    val relations: List<String>,
    val constants: List<String>
)
