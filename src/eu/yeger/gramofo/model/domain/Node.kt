package eu.yeger.gramofo.model.domain

/**
 * Represents a node of a domain [Graph].
 *
 * @property name The name of the node.
 * @property relations [List] of relations assigned to this node. Each must start with an uppercase letter.
 * @property constants [List] of constants assigned to this node. Each must start with a lowercase letter.
 * @constructor Creates a [Node] with the given parameters.
 *
 * @author Jan Müller
 */
data class Node(
    val name: String,
    val relations: List<String>,
    val constants: List<String>
)
