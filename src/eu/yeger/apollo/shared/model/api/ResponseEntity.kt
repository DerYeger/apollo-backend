package eu.yeger.apollo.shared.model.api

import io.ktor.http.*

public data class ResponseEntity<T : Any>(val status: HttpStatusCode, val data: T)

public fun <T : Any> ok(data: T): ResponseEntity<T> =
  ResponseEntity(HttpStatusCode.OK, data)

public fun <T : Any> created(data: T): ResponseEntity<T> =
  ResponseEntity(HttpStatusCode.Created, data)

public fun conflict(translationDTO: TranslationDTO): ResponseEntity<TranslationDTO> =
  ResponseEntity(HttpStatusCode.Conflict, translationDTO)

public fun notFound(translationDTO: TranslationDTO): ResponseEntity<TranslationDTO> =
  ResponseEntity(HttpStatusCode.NotFound, translationDTO)

public fun unauthorized(translationDTO: TranslationDTO): ResponseEntity<TranslationDTO> =
  ResponseEntity(HttpStatusCode.Unauthorized, translationDTO)

public fun unprocessableEntity(translationDTO: TranslationDTO): ResponseEntity<TranslationDTO> =
  ResponseEntity(HttpStatusCode.UnprocessableEntity, translationDTO)
