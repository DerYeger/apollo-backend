package eu.yeger.apollo.assignment.model.api

import com.github.michaelbull.result.get
import eu.yeger.apollo.assignment.model.domain.Assignment
import eu.yeger.apollo.fol.parser.parseFormula
import kotlinx.serialization.Serializable

@Serializable
public data class ApiAssignment(val id: String, val title: String, val formula: String, val description: String? = null)

public fun ApiAssignment.toDomainAssignment(): Assignment {
  return Assignment(
    id = id,
    title = title,
    rawFormula = formula,
    formulaHead = parseFormula(formula).get()!!,
    description = description,
  )
}
