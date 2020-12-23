package eu.yeger.gramofo.fol.graph

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import eu.yeger.gramofo.fol.formula.FOLFormula

data class ModelCheckerTrace(
    val formula: FOLFormula,
    val text: String,
    val children: List<ModelCheckerTrace>
)

typealias ModelCheckerResult = Result<ModelCheckerTrace, ModelCheckerTrace>
typealias ModelCheckerSuccess = Ok<ModelCheckerTrace>
typealias ModelCheckerFailure = Err<ModelCheckerTrace>

fun List<ModelCheckerResult>.split(): Pair<List<ModelCheckerTrace>, List<ModelCheckerTrace>> {
    return groupBy(
        keySelector = { it is ModelCheckerSuccess },
        valueTransform = { it.trace }
    ).let {
        val valid = it[true] ?: emptyList()
        val invalid = it[false] ?: emptyList()
        valid to invalid
    }
}

val ModelCheckerResult.trace: ModelCheckerTrace
    get() = when (this) {
        is ModelCheckerSuccess -> value
        is ModelCheckerFailure -> error
    }

fun FOLFormula.validated(text: String, children: List<ModelCheckerTrace>) = Ok(ModelCheckerTrace(this, text, children.toList()))
fun FOLFormula.validated(text: String, vararg children: ModelCheckerTrace) = Ok(ModelCheckerTrace(this, text, children.toList()))

fun FOLFormula.invalidated(text: String, children: List<ModelCheckerTrace>) = Err(ModelCheckerTrace(this, text, children.toList()))
fun FOLFormula.invalidated(text: String, vararg children: ModelCheckerTrace) = Err(ModelCheckerTrace(this, text, children.toList()))
