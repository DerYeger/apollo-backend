package eu.yeger.apollo

import eu.yeger.apollo.assignment.assignmentModule
import eu.yeger.apollo.model_checker.modelCheckerModule
import io.ktor.application.*
import mu.KotlinLogging
import org.koin.ktor.ext.Koin

private val logger = KotlinLogging.logger { }

public fun Application.koinModule() {
  install(Koin) {
    modules(assignmentModule, modelCheckerModule)
  }

  logger.info { "KoinModule installed" }
}
