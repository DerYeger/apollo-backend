package eu.yeger.gramofo.model.api

import com.github.michaelbull.result.Result
import eu.yeger.gramofo.model.dto.TranslationDTO
import io.ktor.http.*

typealias ApiResult<Data> = Result<HttpEntity<Data>, HttpEntity<TranslationDTO>>

class HttpEntity<Data>(val status: HttpStatusCode, val data: Data) {
    companion object {
        fun <Data> Ok(data: Data) = HttpEntity(HttpStatusCode.OK, data)
        fun <Data> UnprocessableEntity(data: Data) = HttpEntity(HttpStatusCode.UnprocessableEntity, data)
    }
}
