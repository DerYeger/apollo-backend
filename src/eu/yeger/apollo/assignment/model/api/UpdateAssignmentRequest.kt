package eu.yeger.apollo.assignment.model.api

import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import eu.yeger.apollo.assignment.model.domain.Assignment
import eu.yeger.apollo.fol.parser.parseFormula
import eu.yeger.apollo.shared.model.api.IntermediateResult
import eu.yeger.apollo.shared.model.api.TranslationDTO
import eu.yeger.apollo.shared.model.api.unprocessableEntity
import kotlinx.serialization.Serializable

@Serializable
public data class UpdateAssignmentRequest(
  val id: String,
  val title: String,
  val formula: String,
  val description: String?
)

public fun UpdateAssignmentRequest.toAssignment(): IntermediateResult<Assignment> {
  return parseFormula(formula)
    .mapError { error -> unprocessableEntity(TranslationDTO(error)) }
    .map { parsedFormula ->
      Assignment(
        id = id,
        rawFormula = formula,
        formulaHead = parsedFormula,
        title = title,
        description = description
      )
    }
}
