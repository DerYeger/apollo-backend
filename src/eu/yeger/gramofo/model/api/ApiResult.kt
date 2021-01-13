package eu.yeger.gramofo.model.api

import com.github.michaelbull.result.Result
import eu.yeger.gramofo.model.dto.TranslationDTO
import io.ktor.http.*

/**
 * [Result] that either contains a [HttpResponseEntity] with the given type or an [HttpResponseEntity] containing a [TranslationDTO] for error messages.
 *
 * @param Data The type of successful [Result]s.
 *
 * @author Jan Müller
 */
typealias ApiResult<Data> = Result<HttpResponseEntity<Data>, HttpResponseEntity<TranslationDTO>>

/**
 * Represents an entity used for responding to request.
 *
 * @param Data Type of [data].
 * @property status The status code used for responding.
 * @property data The data used as the response body.
 * @constructor Creates an [HttpResponseEntity] with the given parameters.
 *
 * @author Jan Müller
 */
class HttpResponseEntity<Data>(val status: HttpStatusCode, val data: Data) {
    companion object {

        /**
         * Helper-method for responses with [HttpStatusCode.OK].
         *
         * @param Data Type of [data].
         * @param data The data used as the response body.
         */
        fun <Data> ok(data: Data) = HttpResponseEntity(HttpStatusCode.OK, data)

        /**
         * Helper-method for responses with [HttpStatusCode.UnprocessableEntity].
         *
         * @param Data Type of [data].
         * @param data The data used as the response body.
         */
        fun <Data> unprocessableEntity(data: Data) = HttpResponseEntity(HttpStatusCode.UnprocessableEntity, data)
    }
}
