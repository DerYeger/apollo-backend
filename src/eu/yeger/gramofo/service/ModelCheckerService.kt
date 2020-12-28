package eu.yeger.gramofo.service

import eu.yeger.gramofo.fol.graph.ModelCheckerResult
import eu.yeger.gramofo.model.api.ModelCheckerRequest

interface ModelCheckerService {

    fun checkModel(modelCheckerRequest: ModelCheckerRequest): ModelCheckerResult
}
