package eu.yeger.apollo

import eu.yeger.apollo.assignment.assignmentModule
import eu.yeger.apollo.model_checker.modelCheckerModule
import io.ktor.application.*
import org.koin.ktor.ext.Koin

public fun Application.koinModule() {
  install(Koin) {
    modules(assignmentModule, modelCheckerModule)
  }
}
