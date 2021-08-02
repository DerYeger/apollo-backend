package eu.yeger.apollo

import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.model.api.toDomainAssignment
import eu.yeger.apollo.assignment.service.AssignmentService
import eu.yeger.apollo.user.service.UserService
import io.ktor.application.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger { }

public fun Application.initializationModule() {
  val assignmentService: AssignmentService by inject()
  runBlocking {
    val firstExampleAssignment = ApiAssignment(
      id = "first-example",
      title = "First Example",
      formula = "exists x. forall y. R(x, y)",
      description = null
    ).toDomainAssignment()
    assignmentService.create(firstExampleAssignment)
    val secondExampleAssignment = ApiAssignment(
      id = "second-example",
      title = "Second Example",
      formula = "forall x. R(x, c)",
      description = null
    ).toDomainAssignment()
    assignmentService.create(secondExampleAssignment)
  }

  val userService: UserService by inject()
  runBlocking {
    userService.createDefaultUserIfRequired()
  }

  logger.info { "Installation complete" }
}
