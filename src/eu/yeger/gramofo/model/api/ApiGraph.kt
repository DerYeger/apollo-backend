package eu.yeger.gramofo.model.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import eu.yeger.gramofo.fol.graph.Edge
import eu.yeger.gramofo.fol.graph.Graph
import eu.yeger.gramofo.fol.graph.Vertex

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiGraph(
    val nodes: List<ApiNode>,
    val edges: List<ApiEdge>
)

fun ApiGraph.toDomainModel(): Graph {
    val vertices = nodes.associate { node ->
        node.name to Vertex(
            name = node.name,
            relations = node.relations,
            constants = node.constants
        )
    }
    val domainEdges = edges.map { edge ->
        Edge(
            source = vertices[edge.source]!!,
            target = vertices[edge.target]!!,
            relations = edge.relations,
            functions = edge.functions
        )
    }
    return Graph(vertices.values.toList(), domainEdges)
}
