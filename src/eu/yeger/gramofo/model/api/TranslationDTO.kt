package eu.yeger.gramofo.model.api

data class TranslationDTO(
    val key: String,
    val params: Map<String, String> = emptyMap()
)
