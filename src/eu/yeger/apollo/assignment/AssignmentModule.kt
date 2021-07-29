package eu.yeger.apollo.assignment

import eu.yeger.apollo.assignment.repository.AssignmentRepository
import eu.yeger.apollo.assignment.repository.InMemoryAssignmentRepository
import eu.yeger.apollo.assignment.service.AssignmentService
import eu.yeger.apollo.assignment.service.DefaultAssignmentService
import org.koin.core.module.Module
import org.koin.dsl.module

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
    InMemoryAssignmentRepository()
  }
}
