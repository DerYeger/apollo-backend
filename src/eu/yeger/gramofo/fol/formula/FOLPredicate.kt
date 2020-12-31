package eu.yeger.gramofo.fol.formula

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

class FOLPredicate
private constructor(
    name: String,
    children: Set<FOLFormula>,
    private val isInfix: Boolean
) : FOLFormula(
    name = name,
    children = children
) {

    private constructor(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ) : this(
        name = name,
        children = setOf(leftOperand, rightOperand),
        isInfix = true
    )

    override fun checkModel(
        graph: Graph,
        symbolTable: SymbolTable,
        variableAssignments: Map<String, Node>,
        shouldBeModel: Boolean,
    ): ModelCheckerTrace {
        return when (children.size) {
            1 -> checkUnaryRelation(symbolTable, variableAssignments, shouldBeModel)
            2 -> checkBinaryRelation(symbolTable, variableAssignments, shouldBeModel)
            else -> throw ModelCheckerException("[ModelChecker][Internal error] Found predicate with to many children.")
        }
    }

    private fun FOLFormula.checkUnaryRelation(
        symbolTable: SymbolTable,
        variableAssignments: Map<String, Node>,
        shouldBeModel: Boolean,
    ): ModelCheckerTrace {
        val node = getChildAt(0).interpret(symbolTable, variableAssignments)
        val translationParams = mapOf("relation" to name, "node" to node.name)
        return when (symbolTable.unarySymbols[name]!!.contains(node)) {
            true -> validated(TranslationDTO("api.relation.unary.valid", translationParams), variableAssignments, shouldBeModel)
            false -> invalidated(TranslationDTO("api.relation.unary.invalid", translationParams), variableAssignments, shouldBeModel)
        }
    }

    private fun FOLFormula.checkBinaryRelation(
        symbolTable: SymbolTable,
        variableAssignments: Map<String, Node>,
        shouldBeModel: Boolean,
    ): ModelCheckerTrace {
        val left = getChildAt(0).interpret(symbolTable, variableAssignments)
        val right = getChildAt(1).interpret(symbolTable, variableAssignments)
        val translationParams = mapOf(
            "firstTerm" to getChildAt(0).toString(variableAssignments),
            "secondTerm" to getChildAt(1).toString(variableAssignments),
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
        val sb = StringBuilder()
        if (isInfix) {
            sb.append(getChildAt(0).getFormulaString(variableAssignments))
            sb.append(specialNames.getOrDefault(name, name))
            sb.append(getChildAt(1).getFormulaString(variableAssignments))
        } else {
            sb.append(specialNames.getOrDefault(name, name))
            sb.append("(")
            if (children.isNotEmpty()) {
                sb.append(getChildAt(0).getFormulaString(variableAssignments))
                children.drop(1).forEach { child: FOLFormula -> sb.append(", ").append(child.getFormulaString(variableAssignments)) }
            }
            sb.append(")")
        }
        maybeWrapBracketsAndDot(sb)
        return sb.toString()
    }

    companion object {
        fun prefixPredicate(name: String, children: Set<FOLFormula>): FOLPredicate {
            return FOLPredicate(name = name, children = children, isInfix = false)
        }

        fun infixPredicate(name: String, leftOperand: FOLFormula, rightOperand: FOLFormula): FOLPredicate {
            return FOLPredicate(name = name, leftOperand = leftOperand, rightOperand = rightOperand)
        }
    }
}
