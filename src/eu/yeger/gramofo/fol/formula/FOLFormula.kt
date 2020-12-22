package eu.yeger.gramofo.fol.formula

import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * This is the super class of all formula types.
 */
abstract class FOLFormula(
    type: FOLType?,
    hasBrackets: Boolean,
    hasDot: Boolean,
    name: String,
    children: LinkedHashSet<out FOLFormula>? = null,
) {

    val children = children ?: LinkedHashSet()
    /**
     * Specifies the type of the formula. This should match with the corresponding subclass.
     * @return .
     */
    // ////////////////// getter und setter //////////////////////
    var type: FOLType?

    /**
     * The parent is set automatically, if one formula ist set as child of some parent formula.
     * It indicates, that this formula is a child of the formula, which is assigned to parent.
     * @return .
     */
    var parent: FOLFormula?
    var hasBrackets: Boolean
    var hasDot: Boolean
    var name: String
    var variables: ArrayList<FOLBoundVariable>? = null
        private set

    private fun addReferentialIntegrity() {

//        parent.addListener( (observable, oldValue, newValue ) -> {
//            if (oldValue != null){
//                oldValue.getChildren().remove(this);
//            }
//            if (newValue != null){
//                newValue.getChildren().add(this);
//            }
//        });
//
//        children.addListener( (SetChangeListener<FOLFormula>) change -> {
//            if(change.wasRemoved()){
//                change.getElementRemoved().setParent(null);
//            }
//            if(change.wasAdded()){
//                change.getElementAdded().setParent(this);
//            }
//        });
    }

    // ////////////////// operation and information //////////////////////
    abstract override fun toString(): String

    /**
     * @return the child at the specified position of this FOLFormula or a DUMMY, if there is no child at this position.
     */
    fun getChildAt(position: Int): FOLFormula {
        val list = ArrayList(children)
        return if (list.size > position) {
            list[position]
        } else {
            DUMMY
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

    private fun variables(): ArrayList<FOLBoundVariable> {
        val list = ArrayList<FOLBoundVariable>()
        for (i in children.indices) {
            val child = getChildAt(i)
            if (this is FOLQuantifier && child is FOLBoundVariable) {
                list.add(child)
            }
            list.addAll(child.variables())
        }
        return list
    }

    fun containsImplication(): Boolean {
        if (type == FOLType.Implication || type == FOLType.BiImplication) {
            return true
        } else if (children.isNotEmpty()) {
            for (folFormula in children) {
                if (folFormula.containsImplication()) {
                    return true
                }
            }
        }
        return false
    }

    fun countVariables() {
        // Remove duplicates
//        this.variables = new ArrayList<>(new LinkedHashSet<>(variables()));
        variables = variables()
    }

    val clearChildren: Set<FOLFormula>?
        get() {
            val toRemove = ArrayList<FOLFormula>()
            if (this is FOLPredicate) {
                return LinkedHashSet()
            }
            for (folFormula in children) {
                if (this is FOLQuantifier && folFormula is FOLBoundVariable) {
                    toRemove.add(folFormula)
                }
            }
            children.removeAll(toRemove)
            return children
        }

    companion object {
        const val DOT = "..."
        const val TT = "tt"
        const val FF = "ff"
        const val NOT = "\u00AC"
        const val AND = "\u2227"
        const val OR = "\u2228"
        const val IMPLICATION = "\u2192"
        const val BI_IMPLICATION = "\u2194"
        val LOGICAL_OPERANDS = listOf(NOT, AND, OR, IMPLICATION, BI_IMPLICATION)
        const val EXISTS = "\u2203"
        const val FOR_ALL = "\u2200"
        val QUANTIFIERS = listOf(EXISTS, FOR_ALL)
        const val INFIX_EQUALITY = "=" // equal sign with a dot on top
        val DUMMY: FOLFormula = object : FOLFormula(null, false, false, "?") {
            override fun toString(): String {
                return name
            }
        }
    }

    init {
        this.type = type
        parent = null
        this.hasBrackets = hasBrackets
        this.hasDot = hasDot
        this.name = name
        addReferentialIntegrity()
    }
}
