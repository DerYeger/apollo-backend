package eu.yeger.gramofo.routing

import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.service.ModelCheckerService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.modelCheckerRoutes() {
    val modelCheckerService: ModelCheckerService by inject()

    post("modelchecker") {
        val request = call.receive<ModelCheckerRequest>()
        call.respond(modelCheckerService.checkModel(request))
    }
}
