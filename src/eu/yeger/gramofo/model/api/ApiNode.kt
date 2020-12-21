package eu.yeger.gramofo.model.api

data class ApiNode(
    val name: String,
    val relations: List<String>,
    val constants: List<String>
)
