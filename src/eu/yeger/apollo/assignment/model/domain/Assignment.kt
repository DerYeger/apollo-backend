package eu.yeger.apollo.assignment.model.domain

import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.model.persistence.PersistentAssignment
import eu.yeger.apollo.shared.model.fol.FormulaHead

public data class Assignment(val id: String, val title: String, val rawFormula: String, val formulaHead: FormulaHead, val description: String?)

public fun Assignment.toApiModel(): ApiAssignment {
  return ApiAssignment(
    id = id,
    title = title,
    formula = formulaHead.formula.toString(),
    description = description,
  )
}

public fun Assignment.toPersistenceModel(): PersistentAssignment {
  return PersistentAssignment(
    id = id,
    title = title,
    rawFormula = rawFormula,
    description = description,
  )
}
