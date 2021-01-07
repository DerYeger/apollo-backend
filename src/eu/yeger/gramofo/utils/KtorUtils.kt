package eu.yeger.gramofo.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import eu.yeger.gramofo.model.api.ApiResult
import eu.yeger.gramofo.model.api.HttpEntity
import eu.yeger.gramofo.model.dto.TranslationDTO
import io.ktor.application.*
import io.ktor.response.*

suspend fun <T : Any> ApplicationCall.respondWithResult(result: ApiResult<T>) {
    when (result) {
        is Ok<HttpEntity<T>> -> respond(result.value.status, result.value.data)
        is Err<HttpEntity<TranslationDTO>> -> respond(result.error.status, mapOf("message" to result.error.data))
    }
}
