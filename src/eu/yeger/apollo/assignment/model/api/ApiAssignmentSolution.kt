package eu.yeger.apollo.assignment.model.api

import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import eu.yeger.apollo.assignment.model.domain.AssignmentSolution
import eu.yeger.apollo.shared.model.api.ApiGraph
import eu.yeger.apollo.shared.model.api.IntermediateResult
import eu.yeger.apollo.shared.model.api.toDomainModel
import eu.yeger.apollo.shared.model.api.unprocessableEntity
import kotlinx.serialization.Serializable

@Serializable
public data class ApiAssignmentSolution(val assignmentId: String, val firstGraph: ApiGraph, val secondGraph: ApiGraph)

public fun ApiAssignmentSolution.toDomainModel(): IntermediateResult<AssignmentSolution> = binding {
  val firstGraph = firstGraph.toDomainModel().mapError { translationDTO -> unprocessableEntity(translationDTO) }.bind()
  val secondGraph = secondGraph.toDomainModel().mapError { translationDTO -> unprocessableEntity(translationDTO) }.bind()
  AssignmentSolution(
    assignmentId,
    firstGraph = firstGraph,
    secondGraph = secondGraph,
  )
}
