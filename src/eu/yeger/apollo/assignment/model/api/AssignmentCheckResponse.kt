package eu.yeger.apollo.assignment.model.api

import eu.yeger.apollo.shared.model.api.Feedback
import eu.yeger.apollo.shared.model.fol.ModelCheckerTrace
import kotlinx.serialization.Serializable

@Serializable
public data class AssignmentCheckResponse(
  val correct: Boolean,
  val feedback: Feedback?,
  val firstTrace: ModelCheckerTrace?,
  val secondTrace: ModelCheckerTrace?
)
