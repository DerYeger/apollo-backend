package eu.yeger.gramofo.model.api

import eu.yeger.gramofo.fol.graph.ModelCheckerResult

data class ModelCheckerResponse(
    val error: String? = null,
    val result: ModelCheckerResult? = null
)
