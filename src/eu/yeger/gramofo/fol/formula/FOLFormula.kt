package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.graph.Node

/**
 * This is the super class of all formula types.
 * @property type Specifies the type of the formula. This should match with the corresponding subclass.
 */
abstract class FOLFormula(
    val type: FOLType?,
    val name: String,
    val children: Set<FOLFormula> = emptySet(),
) {
    var hasBrackets: Boolean = false
    var hasDot: Boolean = false

    abstract fun getFormulaString(variableAssignments: Map<String, Node>): String

    final override fun toString(): String {
        return toString(emptyMap())
    }

    fun toString(variableAssignments: Map<String, Node>): String {
        return getFormulaString(variableAssignments).removePrefix(". ").removeSurrounding(prefix = "(", suffix = ")")
    }

    /**
     * @return the child at the specified position of this FOLFormula or a Dummy, if there is no child at this position.
     */
    fun getChildAt(position: Int): FOLFormula {
        val list = ArrayList(children)
        return if (list.size > position) {
            list[position]
        } else {
            Dummy
        }
    }

    /**
     * If this formula has brackets and/or a dot it will be added to the StringBuilder
     * @param sb a StringBuilder in which to add brackets and and dot
     * @return the given StringBuilder sb
     */
    fun maybeWrapBracketsAndDot(sb: StringBuilder): StringBuilder {
        sb.insert(0, if (hasBrackets) "(" else "")
        sb.insert(0, if (hasDot) ". " else "")
        sb.append(if (hasBrackets) ")" else "")
        return sb
    }

    companion object {
        const val TT = "tt"
        const val FF = "ff"
        const val NOT = "\u00AC"
        const val AND = "\u2227"
        const val OR = "\u2228"
        const val IMPLICATION = "\u2192"
        const val BI_IMPLICATION = "\u2194"
        const val EXISTS = "\u2203"
        const val FOR_ALL = "\u2200"
        const val INFIX_EQUALITY = "=" // equal sign with a dot on top
    }

    object Dummy : FOLFormula(
        type = null,
        name = "?",
    ) {
        override fun getFormulaString(variableAssignments: Map<String, Node>): String {
            return "?"
        }
    }
}
