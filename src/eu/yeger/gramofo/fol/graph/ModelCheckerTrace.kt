package eu.yeger.gramofo.fol.graph

import eu.yeger.gramofo.fol.formula.FOLFormula

data class ModelCheckerTrace(
    val formula: FOLFormula,
    val text: String,
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

fun FOLFormula.validated(text: String, children: List<ModelCheckerTrace>) = ModelCheckerTrace(this, text, true, children.toList())
fun FOLFormula.validated(text: String, vararg children: ModelCheckerTrace) = ModelCheckerTrace(this, text, true, children.toList())

fun FOLFormula.invalidated(text: String, children: List<ModelCheckerTrace>) = ModelCheckerTrace(this, text, false, children.toList())
fun FOLFormula.invalidated(text: String, vararg children: ModelCheckerTrace) = ModelCheckerTrace(this, text, false, children.toList())
