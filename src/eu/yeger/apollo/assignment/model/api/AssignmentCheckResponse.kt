package eu.yeger.apollo.assignment.model.api

import eu.yeger.apollo.shared.model.fol.ModelCheckerTrace
import kotlinx.serialization.Serializable

@Serializable
public data class AssignmentCheckResponse(
  val correct: Boolean,
  val firstTrace: ModelCheckerTrace?,
  val secondTrace: ModelCheckerTrace?
)
