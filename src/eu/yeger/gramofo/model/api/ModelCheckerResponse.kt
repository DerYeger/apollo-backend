package eu.yeger.gramofo.model.api

import eu.yeger.gramofo.fol.graph.ModelCheckerTrace

data class ModelCheckerResponse(
    val error: String? = null,
    val result: ModelCheckerTrace? = null
)
