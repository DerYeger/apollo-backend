package eu.yeger.gramofo.model.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import eu.yeger.gramofo.fol.graph.Edge
import eu.yeger.gramofo.fol.graph.Graph
import eu.yeger.gramofo.fol.graph.Node

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiGraph(
    val nodes: List<ApiNode>,
    val edges: List<ApiEdge>
)

fun ApiGraph.toDomainModel(): Result<Graph, TranslationDTO> = binding {
    val domainNodes = nodes.toDomainNodes().bind()
    val domainEdges = edges.toDomainEdges(domainNodes).bind()
    Graph(domainNodes, domainEdges)
}

private fun List<ApiNode>.toDomainNodes(): Result<List<Node>, TranslationDTO> = binding {
    map { node ->
        Node(
            name = node.name,
            relations = node.relations.validatedRelations().bind(),
            constants = node.constants.validatedConstants().bind()
        )
    }.also {
        if (it.isEmpty()) {
            Err(TranslationDTO("api.error.empty-graph")).bind<List<Node>>()
        }
    }
}

private fun List<ApiEdge>.toDomainEdges(nodes: List<Node>): Result<List<Edge>, TranslationDTO> = binding {
    val nodeMap = nodes.associateBy(Node::name)
    map { edge ->
        Edge(
            source = nodeMap[edge.source] ?: Err(TranslationDTO("api.error.missing-node", "node" to edge.source)).bind<Node>(),
            target = nodeMap[edge.target] ?: Err(TranslationDTO("api.error.missing-node", "node" to edge.target)).bind<Node>(),
            relations = edge.relations.validatedRelations().bind(),
            functions = edge.functions.validatedFunctions().bind()
        )
    }
}

private fun List<String>.validatedRelations(): Result<List<String>, TranslationDTO> {
    forEach { relation ->
        if (!(relation.isNotBlank() && relation.first().isUpperCase())) {
            return@validatedRelations Err(TranslationDTO("api.error.invalid-relation", "relation" to relation))
        }
    }
    return Ok(this)
}

private fun List<String>.validatedFunctions(): Result<List<String>, TranslationDTO> {
    forEach { function ->
        if (!(function.isNotBlank() && function.first().isLowerCase())) {
            return@validatedFunctions Err(TranslationDTO("api.error.invalid-function", "function" to function))
        }
    }
    return Ok(this)
}

private fun List<String>.validatedConstants(): Result<List<String>, TranslationDTO> {
    forEach { constant ->
        if (!(constant.isNotBlank() && constant.first().isLowerCase())) {
            return@validatedConstants Err(TranslationDTO("api.error.invalid-constant", "constant" to constant))
        }
    }
    return Ok(this)
}
