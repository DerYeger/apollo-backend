package eu.yeger.gramofo.model.api

data class ModelCheckerRequest(
    val formula: String,
    val graph: ApiGraph,
    val language: String
)
