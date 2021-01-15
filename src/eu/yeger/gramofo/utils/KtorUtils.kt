package eu.yeger.gramofo.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import eu.yeger.gramofo.model.api.ApiResult
import eu.yeger.gramofo.model.api.HttpResponseEntity
import eu.yeger.gramofo.model.dto.TranslationDTO
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

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
public suspend inline fun <reified T : Any> ApplicationCall.respondWithResult(result: ApiResult<T>) {
    try {
        when (result) {
            is Ok<HttpResponseEntity<T>> -> respond(result.value.status, result.value.data)
            is Err<HttpResponseEntity<TranslationDTO>> -> respond(result.error.status, mapOf("message" to result.error.data))
        }
    } catch (outOfMemoryError: OutOfMemoryError) {
        respond(HttpStatusCode.InsufficientStorage, mapOf("message" to "api.error.response-too-big"))
    }
}
