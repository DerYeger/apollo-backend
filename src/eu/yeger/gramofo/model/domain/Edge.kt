package eu.yeger.gramofo.model.domain

/**
 * Represents an edge between two [Node]s of a domain [Graph].
 *
 * @property source The source [Node].
 * @property target The target [Node].
 * @property relations [List] of relations assigned to this edge. Each must start with an uppercase letter.
 * @property functions [List] of functions assigned to this edge. Each must start with a lowercase letter.
 * @constructor Creates a [Edge] with the given parameters.
 *
 * @author Jan Müller
 */
data class Edge(
    val source: Node,
    val target: Node,
    val relations: List<String>,
    val functions: List<String>
)
