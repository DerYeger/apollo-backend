package eu.yeger.gramofo.routing

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import eu.yeger.gramofo.fol.graph.ModelCheckerTrace
import eu.yeger.gramofo.model.api.ModelCheckerRequest
import eu.yeger.gramofo.service.ModelCheckerService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.modelCheckerRoutes() {
    val modelCheckerService: ModelCheckerService by inject()

    post("modelchecker") {
        val request = call.receive<ModelCheckerRequest>()
        when (val result = modelCheckerService.checkModel(request)) {
            is Ok<ModelCheckerTrace> -> call.respond(result.value)
            is Err<String> -> call.respond(HttpStatusCode.UnprocessableEntity, mapOf("message" to result.error))
        }
    }
}
