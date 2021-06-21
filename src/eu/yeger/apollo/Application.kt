package eu.yeger.apollo

import eu.yeger.apollo.di.serviceModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level

/**
 * Starts the server-engine.
 *
 * @param args Arguments of this application.
 *
 * @author Jan Müller
 */
public fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Installs Koin (dependency-injection), logging, (de-)serialization and CORS features.
 *
 * @receiver The [Application] the module will be installed in.
 *
 * @author Jan Müller
 */
@Suppress("unused") // Referenced in application.conf
public fun Application.mainModule() {
  install(Koin) {
    modules(serviceModule)
  }

  install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
  }

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
    anyHost()
    allowNonSimpleContentTypes = true
    allowSameOrigin = true
  }
}
