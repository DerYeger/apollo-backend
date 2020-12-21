package eu.yeger.gramofo.model.api

import eu.yeger.gramofo.fol.graph.Edge
import eu.yeger.gramofo.fol.graph.Graph
import eu.yeger.gramofo.fol.graph.Vertex

data class ApiGraph(
    val name: String,
    val description: String,
    val lastEdit: Long,
    val nodes: List<ApiNode>,
    val edges: List<ApiEdge>
)

fun ApiGraph.toDomainModel(): Graph {
    val graph = Graph()
    val vertices = nodes.associate { node ->
        node.name to Vertex().apply {
            readableName = node.name
            stringAttachments = node.relations + node.constants
        }
    }
    val domainEdges = edges.map { edge ->
        Edge().apply {
            fromVertex = vertices[edge.source]
            toVertex = vertices[edge.target]
            stringAttachments = edge.relations + edge.functions
        }
    }
    graph.vertices.addAll(vertices.values)
    graph.edges.addAll(domainEdges)
    return graph
}
