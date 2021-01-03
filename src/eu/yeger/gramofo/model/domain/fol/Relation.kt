package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.ModelCheckerTrace
import eu.yeger.gramofo.fol.SymbolTable
import eu.yeger.gramofo.model.domain.Edge
import eu.yeger.gramofo.model.domain.Graph
import eu.yeger.gramofo.model.domain.Node
import eu.yeger.gramofo.model.dto.TranslationDTO

private val specialNames = mapOf(
    "=" to "\u2250",
    "<=" to "\u2264",
    ">=" to "\u2265"
)

sealed class Relation(name: String) : Formula(name) {

    class Unary(name: String, val term: Term) : Relation(name) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val node = term.evaluate(symbolTable, variableAssignments)
            val translationParams = mapOf("relation" to name, "node" to node.name)
            return when (symbolTable.unarySymbols[name]!!.contains(node)) {
                true -> validated(
                    TranslationDTO("api.relation.unary.valid", translationParams),
                    variableAssignments,
                    shouldBeModel
                )
                false -> invalidated(
                    TranslationDTO("api.relation.unary.invalid", translationParams),
                    variableAssignments,
                    shouldBeModel
                )
            }
        }

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            val relation = specialNames.getOrDefault(name, name)
            val termString = term.getFormulaString(variableAssignments)
            return "$relation($termString)".maybeWrapBracketsAndDot()
        }
    }

    class Binary(name: String, val firstTerm: Term, val secondTerm: Term, val isInfix: Boolean) : Relation(name) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = firstTerm.evaluate(symbolTable, variableAssignments)
            val right = secondTerm.evaluate(symbolTable, variableAssignments)
            val translationParams = mapOf(
                "firstTerm" to firstTerm.toString(variableAssignments),
                "secondTerm" to secondTerm.toString(variableAssignments),
                "firstResult" to left.name,
                "secondResult" to right.name
            )
            return if (name == INFIX_EQUALITY) {
                when (left) {
                    right -> validated(
                        TranslationDTO("api.relation.equality.valid", translationParams),
                        variableAssignments,
                        shouldBeModel
                    )
                    else -> invalidated(
                        TranslationDTO("api.relation.equality.invalid", translationParams),
                        variableAssignments,
                        shouldBeModel
                    )
                }
            } else {
                val binaryTranslationParams = translationParams + ("relation" to name)
                when (symbolTable.binarySymbols[name]!!.any { edge: Edge -> edge.source == left && edge.target == right }) {
                    true -> validated(
                        TranslationDTO("api.relation.binary.valid", binaryTranslationParams),
                        variableAssignments,
                        shouldBeModel
                    )
                    else -> invalidated(
                        TranslationDTO("api.relation.binary.invalid", binaryTranslationParams),
                        variableAssignments,
                        shouldBeModel
                    )
                }
            }
        }

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            val relation = specialNames.getOrDefault(name, name)
            val first = firstTerm.getFormulaString(variableAssignments)
            val second = secondTerm.getFormulaString(variableAssignments)
            return when (isInfix) {
                true -> "$first $relation $second".maybeWrapBracketsAndDot()
                false -> "$relation($first, $second)".maybeWrapBracketsAndDot()
            }
        }
    }
}
