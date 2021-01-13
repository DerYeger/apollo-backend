package eu.yeger.gramofo.model.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for translations with optional parameters.
 *
 * @property key The key of the translation.
 * @property params Optional parameters of the translation.
 * @constructor Creates a [TranslationDTO] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
data class TranslationDTO(
    val key: String,
    val params: Map<String, String>? = null
) {
    constructor(key: String, vararg params: Pair<String, String>) : this(key, mapOf(*params).takeUnless { it.isEmpty() })
}
