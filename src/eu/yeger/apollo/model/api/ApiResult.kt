package eu.yeger.apollo.model.api

import com.github.michaelbull.result.Result
import eu.yeger.apollo.model.dto.TranslationDTO
import io.ktor.http.*

/**
 * [Result] that either contains a [HttpResponseEntity] with the given type or an [HttpResponseEntity] containing a [TranslationDTO] for error messages.
 *
 * @param Data The type of successful [Result]s.
 *
 * @author Jan Müller
 */
public typealias ApiResult<Data> = Result<HttpResponseEntity<Data>, HttpResponseEntity<TranslationDTO>>

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
public data class HttpResponseEntity<Data>(val status: HttpStatusCode, val data: Data) {

  /**
   * Companion object that contains helper-methods.
   */
  public companion object {

    /**
     * Helper-method for responses with [HttpStatusCode.OK].
     *
     * @param Data Type of [data].
     * @param data The data used as the response body.
     */
    public fun <Data> ok(data: Data): HttpResponseEntity<Data> =
      HttpResponseEntity(HttpStatusCode.OK, data)

    /**
     * Helper-method for responses with [HttpStatusCode.UnprocessableEntity].
     *
     * @param Data Type of [data].
     * @param data The data used as the response body.
     */
    public fun <Data> unprocessableEntity(data: Data): HttpResponseEntity<Data> =
      HttpResponseEntity(HttpStatusCode.UnprocessableEntity, data)
  }
}
