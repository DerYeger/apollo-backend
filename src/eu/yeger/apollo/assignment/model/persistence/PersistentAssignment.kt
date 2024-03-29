package eu.yeger.apollo.assignment.model.persistence

import com.github.michaelbull.result.get
import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.model.domain.Assignment
import eu.yeger.apollo.assignment.model.domain.toApiAssignment
import eu.yeger.apollo.fol.parser.parseFormula
import eu.yeger.apollo.shared.model.persistence.Entity

public data class PersistentAssignment(override val id: String, val title: String, val formula: String, val description: String?) : Entity

public fun PersistentAssignment.toApiAssignment(): ApiAssignment {
  return toDomainAssignment().toApiAssignment()
}

public fun PersistentAssignment.toDomainAssignment(): Assignment {
  return Assignment(
    id = id,
    title = title,
    rawFormula = formula,
    formulaHead = parseFormula(formula).get()!!,
    description = description,
  )
}
