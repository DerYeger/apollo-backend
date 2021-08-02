package eu.yeger.apollo.assignment

import com.github.michaelbull.result.andThen
import eu.yeger.apollo.assignment.model.api.ApiAssignmentSolution
import eu.yeger.apollo.assignment.model.api.CreateAssignmentRequest
import eu.yeger.apollo.assignment.model.api.UpdateAssignmentRequest
import eu.yeger.apollo.assignment.model.api.toAssignment
import eu.yeger.apollo.assignment.service.AssignmentService
import eu.yeger.apollo.utils.delete
import eu.yeger.apollo.utils.get
import eu.yeger.apollo.utils.getParameter
import eu.yeger.apollo.utils.post
import eu.yeger.apollo.utils.put
import io.ktor.auth.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

public fun Route.assignmentRoutes() {
  val assignmentService: AssignmentService by inject()

  route("assignments") {
    get {
      assignmentService.getAll()
    }

    authenticate {
      post { request: CreateAssignmentRequest ->
        request
          .toAssignment()
          .andThen { assignment -> assignmentService.create(assignment) }
      }

      put { request: UpdateAssignmentRequest ->
        request
          .toAssignment()
          .andThen { assignment -> assignmentService.create(assignment) }
      }
    }

    route("{id}") {
      get {
        assignmentService.getById(getParameter("id"))
      }

      post("solution") { assignmentSolution: ApiAssignmentSolution ->
        assignmentService.checkAssignment(assignmentSolution)
      }

      authenticate {
        delete {
          assignmentService.deleteById(getParameter("id"))
        }
      }
    }
  }
}
