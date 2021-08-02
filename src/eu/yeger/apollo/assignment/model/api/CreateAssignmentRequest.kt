package eu.yeger.apollo.assignment.model.api

import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import eu.yeger.apollo.assignment.model.domain.Assignment
import eu.yeger.apollo.fol.parser.parseFormula
import eu.yeger.apollo.shared.model.api.IntermediateResult
import eu.yeger.apollo.shared.model.api.TranslationDTO
import eu.yeger.apollo.shared.model.api.unprocessableEntity
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
public data class CreateAssignmentRequest(
  val title: String,
  val formula: String,
  val description: String? = null
)

public fun CreateAssignmentRequest.toAssignment(): IntermediateResult<Assignment> {
  return parseFormula(formula)
    .mapError { error -> unprocessableEntity(TranslationDTO(error)) }
    .map { parsedFormula ->
      Assignment(
        id = UUID.randomUUID().toString(),
        rawFormula = formula,
        formulaHead = parsedFormula,
        title = title,
        description = description
      )
    }
}
