package eu.yeger.apollo

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Installs (de-)serialization and CORS features.
 *
 * @receiver The [Application] the module will be installed in.
 *
 * @author Jan Müller
 */
public fun Application.mainModule() {
  install(ContentNegotiation) {
    json(
      json = Json {
        encodeDefaults = false
        ignoreUnknownKeys = true
      }
    )
  }

  install(CORS) {
    method(HttpMethod.Options)
    method(HttpMethod.Put)
    method(HttpMethod.Delete)
    method(HttpMethod.Patch)
    header(HttpHeaders.Authorization)
    anyHost()
    allowNonSimpleContentTypes = true
    allowSameOrigin = true
  }

  logger.info { "Installation complete" }
}
