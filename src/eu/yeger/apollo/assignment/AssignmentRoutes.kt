package eu.yeger.apollo.assignment

import eu.yeger.apollo.assignment.model.api.ApiAssignmentSolution
import eu.yeger.apollo.assignment.service.AssignmentService
import eu.yeger.apollo.utils.get
import eu.yeger.apollo.utils.getParameter
import eu.yeger.apollo.utils.post
import io.ktor.routing.*
import org.koin.ktor.ext.inject

public fun Route.assignmentRoutes() {
  val assignmentService: AssignmentService by inject()

  route("assignments") {
    get {
      assignmentService.getAll()
    }

    route("{id}") {
      get {
        assignmentService.getById(getParameter("id"))
      }

      post("solution") { assignmentSolution: ApiAssignmentSolution ->
        assignmentService.checkAssignment(assignmentSolution)
      }
    }
  }
}
