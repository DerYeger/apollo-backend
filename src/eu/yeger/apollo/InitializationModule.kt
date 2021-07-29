package eu.yeger.apollo

import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.service.AssignmentService
import io.ktor.application.*
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.inject

public fun Application.initializationModule() {
  val assignmentService: AssignmentService by inject()
  runBlocking {
    val firstExampleAssignment = ApiAssignment(
      id = "first-example",
      title = "First Example",
      formula = "exists x. forall y. R(x, y)",
      description = null
    )
    assignmentService.create(firstExampleAssignment)
    val secondExampleAssignment = ApiAssignment(
      id = "second-example",
      title = "Second Example",
      formula = "forall x. R(x, c)",
      description = null
    )
    assignmentService.create(secondExampleAssignment)
  }
}
