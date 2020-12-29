package eu.yeger.gramofo.service

import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.model.api.ModelCheckerResponse

interface ModelCheckerService {

    fun checkModel(modelCheckerRequest: ModelCheckerRequest): ModelCheckerResponse
}
