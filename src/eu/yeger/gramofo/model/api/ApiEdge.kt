package eu.yeger.gramofo.model.api

data class ApiEdge(
    val source: String,
    val target: String,
    val relations: List<String>,
    val functions: List<String>
)
