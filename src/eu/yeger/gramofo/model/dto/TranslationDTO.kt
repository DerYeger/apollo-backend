package eu.yeger.gramofo.model.dto

data class TranslationDTO(
    val key: String,
    val params: Map<String, String> = emptyMap()
) {
    constructor(key: String, vararg params: Pair<String, String>) : this(key, mapOf(*params))
}
