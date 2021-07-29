package eu.yeger.apollo.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import eu.yeger.apollo.shared.model.api.ApiResult
import eu.yeger.apollo.shared.model.api.ResponseEntity
import eu.yeger.apollo.shared.model.api.TranslationDTO
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.routing.delete as ktorDelete
import io.ktor.routing.get as ktorGet
import io.ktor.routing.post as ktorPost
import io.ktor.routing.put as ktorPut

/**
 * Responds to a call, using the given [ApiResult].
 * If an [OutOfMemoryError] occurs during serialization, it will be reported to the client as a 507 status code.
 *
 * @receiver The [ApplicationCall] that will receive the response.
 * @param T Reified type of the data for successful responses.
 * @param result The [ApiResult] used for the response.
 *
 * @author Jan Müller
 */
public suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondWithResult(result: ApiResult<T>) {
  try {
    when (result) {
      is Ok<ResponseEntity<T>> -> call.respond(result.value.status, result.value.data)
      is Err<ResponseEntity<TranslationDTO>> -> call.respond(result.error.status, mapOf("message" to result.error.data))
    }
  } catch (outOfMemoryError: OutOfMemoryError) {
    call.respond(HttpStatusCode.InsufficientStorage, mapOf("message" to "api.error.response-too-big"))
  }
}

@ContextDsl
public inline fun <reified T : Any> Route.get(
  path: String = "",
  crossinline body: suspend PipelineContext<Unit, ApplicationCall>.() -> ApiResult<T>
): Route {
  return ktorGet(path) {
    respondWithResult(body())
  }
}

@ContextDsl
public inline fun <reified R : Any, reified T : Any> Route.post(
  path: String = "",
  crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> ApiResult<T>
): Route {
  return ktorPost<R>(path) { received ->
    respondWithResult(body(received))
  }
}

@ContextDsl
public inline fun <reified R : Any, reified T : Any> Route.put(
  path: String = "",
  crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(R) -> ApiResult<T>
): Route {
  return ktorPut(path) {
    respondWithResult(body(call.receive()))
  }
}

@ContextDsl
public inline fun <reified T : Any> Route.delete(
  path: String = "",
  crossinline body: suspend PipelineContext<Unit, ApplicationCall>.() -> ApiResult<T>
): Route {
  return ktorDelete(path) {
    respondWithResult(body())
  }
}

public fun PipelineContext<Unit, ApplicationCall>.getParameter(name: String): String {
  return call.parameters[name] ?: throw BadRequestException("")
}
