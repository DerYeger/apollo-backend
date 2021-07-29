package eu.yeger.apollo.assignment.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.mapBoth
import eu.yeger.apollo.assignment.model.persistence.PersistentAssignment
import eu.yeger.apollo.shared.model.api.IntermediateResult
import eu.yeger.apollo.shared.model.api.TranslationDTO
import eu.yeger.apollo.shared.model.api.conflict
import eu.yeger.apollo.shared.model.api.notFound
import eu.yeger.apollo.shared.repository.Repository

public interface AssignmentRepository : Repository<PersistentAssignment> {

  public suspend fun validateAssignmentWithIdExists(id: String): IntermediateResult<PersistentAssignment> {
    return when (val assignment = getById(id)) {
      null -> Err(notFound(TranslationDTO("api.error.assignment.not-found")))
      else -> Ok(assignment)
    }
  }

  public suspend fun validateAssignmentIdIsAvailable(id: String): IntermediateResult<Unit> {
    return validateAssignmentWithIdExists(id)
      .mapBoth(
        success = { Err(conflict(TranslationDTO("api.error.assignment.id-taken"))) },
        failure = { Ok(Unit) }
      )
  }
}
