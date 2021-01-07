package eu.yeger.gramofo.routing

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.routingModule() = routing {
    route("/") {
        get {
            call.respondText("Hello World!", contentType = ContentType.Text.Plain)
        }

        modelCheckerRoutes()
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
            throw cause
        }
//        exception<> { cause ->
//            call.respond(HttpStatusCode.BadRequest)
//            throw cause
//        }
    }
}
