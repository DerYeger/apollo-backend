package eu.yeger.gramofo.routing

import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.service.ModelCheckerService
import eu.yeger.gramofo.utils.respondWithResult
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Appends all routes for ModelChecking to the given [Route].
 *
 * @receiver The base [Route].
 *
 * @author Jan Müller
 */
public fun Route.modelCheckerRoutes() {
    val modelCheckerService: ModelCheckerService by inject()

    post("modelchecker") {
        val request = call.receive<ModelCheckerRequest>()
        val result = modelCheckerService.checkModel(request)
        call.respondWithResult(result)
    }
}
