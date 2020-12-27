package eu.yeger.gramofo.fol.graph

import eu.yeger.gramofo.fol.formula.FOLFormula
import eu.yeger.gramofo.model.api.TranslationDTO

data class ModelCheckerTrace(
    val formula: String,
    val description: TranslationDTO,
    val isModel: Boolean,
    val children: List<ModelCheckerTrace>
)

fun List<ModelCheckerTrace>.split(): Pair<List<ModelCheckerTrace>, List<ModelCheckerTrace>> {
    return groupBy { it.isModel }.let {
        val valid = it[true] ?: emptyList()
        val invalid = it[false] ?: emptyList()
        valid to invalid
    }
}

fun FOLFormula.validated(description: TranslationDTO, children: List<ModelCheckerTrace>) =
    ModelCheckerTrace(this.toString(), description, true, children.toList())
fun FOLFormula.validated(description: TranslationDTO, vararg children: ModelCheckerTrace) =
    ModelCheckerTrace(this.toString(), description, true, children.toList())

fun FOLFormula.invalidated(description: TranslationDTO, children: List<ModelCheckerTrace>) =
    ModelCheckerTrace(this.toString(), description, false, children.toList())
fun FOLFormula.invalidated(description: TranslationDTO, vararg children: ModelCheckerTrace) =
    ModelCheckerTrace(this.toString(), description, false, children.toList())
