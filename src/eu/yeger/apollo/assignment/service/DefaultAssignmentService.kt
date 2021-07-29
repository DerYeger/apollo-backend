package eu.yeger.apollo.assignment.service

import com.github.michaelbull.result.*
import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.model.api.ApiAssignmentSolution
import eu.yeger.apollo.assignment.model.api.AssignmentCheckResponse
import eu.yeger.apollo.assignment.model.api.toDomainModel
import eu.yeger.apollo.assignment.model.domain.toApiModel
import eu.yeger.apollo.assignment.model.domain.toPersistenceModel
import eu.yeger.apollo.assignment.model.persistence.PersistentAssignment
import eu.yeger.apollo.assignment.model.persistence.toApiModel
import eu.yeger.apollo.assignment.model.persistence.toDomainModel
import eu.yeger.apollo.assignment.repository.AssignmentRepository
import eu.yeger.apollo.fol.checkModel
import eu.yeger.apollo.shared.model.api.*
import eu.yeger.apollo.utils.toResult

public class DefaultAssignmentService(private val assignmentRepository: AssignmentRepository) : AssignmentService {

  public override suspend fun getAll(): ApiResult<List<ApiAssignment>> {
    return assignmentRepository
      .getAll()
      .map(PersistentAssignment::toApiModel)
      .toResult(::ok)
  }

  public override suspend fun getById(id: String): ApiResult<ApiAssignment> {
    return assignmentRepository
      .validateAssignmentWithIdExists(id)
      .map(PersistentAssignment::toApiModel)
      .map(::ok)
  }

  public override suspend fun create(apiAssignment: ApiAssignment): ApiResult<ApiAssignment> {
    return assignmentRepository
      .validateAssignmentIdIsAvailable(apiAssignment.id)
      .map { apiAssignment.toDomainModel() }
      .onSuccess { assignment -> assignmentRepository.save(assignment.toPersistenceModel()) }
      .map { assignment -> assignment.toApiModel() }
      .map(::created)
  }

  public override suspend fun checkAssignment(apiSolution: ApiAssignmentSolution): ApiResult<AssignmentCheckResponse> {
    return assignmentRepository.validateAssignmentWithIdExists(apiSolution.assignmentId).andThen { assignment ->
      apiSolution.toDomainModel().map { solution -> assignment.toDomainModel() to solution }
    }
      .andThen { (assignment, solution) ->
        binding {
          val firstCheck = checkModel(
            solution.firstGraph,
            assignment.formulaHead,
            Feedback.Relevant,
            shouldBeModel = true
          ).mapError { translationDTO -> unprocessableEntity(translationDTO) }.bind()
          val secondCheck = checkModel(
            solution.secondGraph,
            assignment.formulaHead,
            Feedback.Relevant,
            shouldBeModel = false
          ).mapError { translationDTO -> unprocessableEntity(translationDTO) }.bind()
          val correct = firstCheck.isModel && secondCheck.isModel.not()
          ok(
            AssignmentCheckResponse(
              correct,
              if (correct) null else Feedback.Relevant,
              if (!correct && firstCheck.isModel.not()) firstCheck else null,
              if (!correct && secondCheck.isModel) secondCheck else null,
            )
          )
        }
      }
  }
}
