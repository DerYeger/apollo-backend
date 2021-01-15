package eu.yeger.gramofo.model.domain.fol

import eu.yeger.gramofo.model.domain.Node

/**
 * Provides helper methods for String representations of FOL entities (formulas and terms).
 *
 * @property name The name of the entity.
 * @constructor Creates an [FOLEntity] with the given name.
 *
 * @author Jan Müller
 */
abstract class FOLEntity(val name: String) {

    /**
     * Indicates that the [String] representation of this entity has brackets. Defaults to false.
     * Mutability is required by the legacy parser.
     */
    var hasBrackets: Boolean = false

    /**
     * Indicates that the [String] representation of this entity has a dot. Defaults to false.
     * Mutability is required by the legacy parser.
     */
    var hasDot: Boolean = false

    /**
     * Returns a raw [String] representation of an [FOLEntity].
     * This method should not be called directly, as it does not include brackets and dots.
     *
     * @see toString(Map, Boolean)
     *
     * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
     * @return The raw [String] representation of this [FOLEntity].
     */
    abstract fun getRawString(variableAssignments: Map<String, Node>): String

    /**
     * Returns a [String] representation with no variable assignments and no brackets or dot at root level.
     *
     * @return The unformatted [String] representation of this [FOLEntity].
     */
    final override fun toString() = toString(emptyMap(), false)

    /**
     * Returns a [String] representation with no variable assignments and optional brackets or dot at root level.
     *
     * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
     * @param wrap Indicates that [hasBrackets] and [hasDot] will be checked to eventually include them in the [String] representation.
     * @return The formatted [String] representation of this [FOLEntity].
     */
    fun toString(variableAssignments: Map<String, Node>, wrap: Boolean): String {
        val formulaString = getRawString(variableAssignments)
        return when (wrap) {
            true -> formulaString.wrapBracketsAndDot()
            false -> formulaString
        }
    }

    /**
     * Wraps a [String] with brackets and a dot, if [hasBrackets] / [hasDot] are true.
     *
     * @receiver The source [String].
     * @return The formatted [String].
     */
    private fun String.wrapBracketsAndDot(): String {
        return when (hasBrackets) {
            true -> "($this)"
            false -> this
        }.let {
            when (hasDot) {
                true -> ". $it"
                false -> it
            }
        }
    }
}
