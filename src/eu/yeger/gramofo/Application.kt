package eu.yeger.gramofo

import eu.yeger.gramofo.di.serviceModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.mainModule() {
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

    install(Compression)

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
