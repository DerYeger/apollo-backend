package eu.yeger.gramofo.model.domain

/**
 * Represents a graph consisting of [Node]s and [Edge]s.
 *
 * @property nodes [List] of [Node]s that are part of the graph.
 * @property edges [List] of [Edge]s that are part of the graph.
 * @constructor Creates a [Graph] with the given [nodes] and [edges].
 *
 * @author Jan Müller
 */
data class Graph(
    val nodes: List<Node>,
    val edges: List<Edge>
)
