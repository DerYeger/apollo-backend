package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.fol.*
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
            val node = term.interpret(symbolTable, variableAssignments)
            val translationParams = mapOf("relation" to name, "node" to node.name)
            return when (symbolTable.unarySymbols[name]!!.contains(node)) {
                true -> validated(TranslationDTO("api.relation.unary.valid", translationParams), variableAssignments, shouldBeModel)
                false -> invalidated(TranslationDTO("api.relation.unary.invalid", translationParams), variableAssignments, shouldBeModel)
            }
        }

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return buildString {
                append(specialNames.getOrDefault(name, name))
                append("(")
                append(term.getFormulaString(variableAssignments))
                append(")")
                maybeWrapBracketsAndDot()
            }
        }
    }

    class Binary(name: String, val firstTerm: Term, val secondTerm: Term, val isInfix: Boolean) : Relation(name) {
        override fun checkModel(
            graph: Graph,
            symbolTable: SymbolTable,
            variableAssignments: Map<String, Node>,
            shouldBeModel: Boolean,
        ): ModelCheckerTrace {
            val left = firstTerm.interpret(symbolTable, variableAssignments)
            val right = secondTerm.interpret(symbolTable, variableAssignments)
            val translationParams = mapOf(
                "firstTerm" to firstTerm.toString(variableAssignments),
                "secondTerm" to secondTerm.toString(variableAssignments),
                "firstResult" to left.name,
                "secondResult" to right.name
            )
            return if (name == INFIX_EQUALITY) {
                when (left) {
                    right -> validated(TranslationDTO("api.relation.equality.valid", translationParams), variableAssignments, shouldBeModel)
                    else -> invalidated(TranslationDTO("api.relation.equality.invalid", translationParams), variableAssignments, shouldBeModel)
                }
            } else {
                val binaryTranslationParams = translationParams + ("relation" to name)
                when (symbolTable.binarySymbols[name]!!.any { edge: Edge -> edge.source == left && edge.target == right }) {
                    true -> validated(TranslationDTO("api.relation.binary.valid", binaryTranslationParams), variableAssignments, shouldBeModel)
                    else -> invalidated(TranslationDTO("api.relation.binary.invalid", binaryTranslationParams), variableAssignments, shouldBeModel)
                }
            }
        }

        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return buildString {
                if (isInfix) {
                    append(firstTerm.getFormulaString(variableAssignments))
                    append(specialNames.getOrDefault(name, name))
                    append(secondTerm.getFormulaString(variableAssignments))
                } else {
                    append(specialNames.getOrDefault(name, name))
                    append("(")
                    append(firstTerm.getFormulaString(variableAssignments))
                    append(", ")
                    append(secondTerm.getFormulaString(variableAssignments))
                    append(")")
                }
                maybeWrapBracketsAndDot()
            }
        }
    }
}
