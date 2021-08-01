package eu.yeger.apollo.assignment

import eu.yeger.apollo.Arguments
import eu.yeger.apollo.assignment.repository.AssignmentRepository
import eu.yeger.apollo.assignment.repository.ExposedAssignmentRepository
import eu.yeger.apollo.assignment.repository.InMemoryAssignmentRepository
import eu.yeger.apollo.assignment.service.AssignmentService
import eu.yeger.apollo.assignment.service.DefaultAssignmentService
import mu.KotlinLogging
import org.koin.core.module.Module
import org.koin.dsl.module

private val kotlinLogger = KotlinLogging.logger { }

/**
 * Koin-module containing assignment services and repositories.
 *
 * @author Jan Müller
 */
public val assignmentModule: Module = module {
  single<AssignmentService> {
    DefaultAssignmentService(get())
  }
  single<AssignmentRepository> {
    if (Arguments.useDatabase) {
      ExposedAssignmentRepository()
    } else {
      InMemoryAssignmentRepository()
    }.also { kotlinLogger.debug { "Using ${it::class.java.simpleName}" } }
  }
}
