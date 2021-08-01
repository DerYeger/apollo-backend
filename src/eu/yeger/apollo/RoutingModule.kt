package eu.yeger.apollo

import eu.yeger.apollo.assignment.assignmentRoutes
import eu.yeger.apollo.model_checker.modelCheckerRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerializationException
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Root routing module of the backend.
 * Includes all routes and defines status pages.
 *
 * @receiver The [Application] the module will be installed in.
 *
 * @author Jan Müller
 */
public fun Application.routingModule(): Routing = routing {
  route("/") {
    get {
      call.respondText("Apollo-Backend is available!", contentType = ContentType.Text.Plain)
    }

    assignmentRoutes()
    modelCheckerRoutes()
  }

  install(StatusPages) {
    exception<Throwable> { cause ->
      call.respond(HttpStatusCode.InternalServerError, cause.message ?: "api.error.unknown")
      throw cause
    }
    exception<SerializationException> { cause ->
      call.respond(HttpStatusCode.BadRequest, cause.message ?: "api.error.unknown")
    }
  }

  logger.info { "RoutingModule installed" }
}
