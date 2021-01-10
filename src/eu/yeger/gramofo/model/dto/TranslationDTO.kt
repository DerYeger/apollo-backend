package eu.yeger.gramofo.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslationDTO(
    val key: String,
    val params: Map<String, String>? = null
) {
    constructor(key: String, vararg params: Pair<String, String>) : this(key, mapOf(*params).takeUnless { it.isEmpty() })
}
