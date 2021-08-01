package eu.yeger.apollo

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.metrics.micrometer.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KotlinLogging
import org.slf4j.event.Level

private val logger = KotlinLogging.logger { }

public fun Application.monitoringModule() {
  install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
  }

  val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  install(MicrometerMetrics) {
    registry = appMicrometerRegistry
  }

  routing {
    get("/metrics-micrometer") {
      call.respond(appMicrometerRegistry.scrape())
    }
  }

  logger.info { "MonitoringModule installed" }
}
