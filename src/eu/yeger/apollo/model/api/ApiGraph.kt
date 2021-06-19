package eu.yeger.apollo.model.api

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import eu.yeger.apollo.model.domain.Edge
import eu.yeger.apollo.model.domain.Graph
import eu.yeger.apollo.model.domain.Node
import eu.yeger.apollo.model.dto.TranslationDTO
import kotlinx.serialization.Serializable

/**
 * Represents a graph consisting of [ApiNode]s and [ApiEdge]s.
 *
 * @property nodes [List] of [ApiNode]s that are part of the graph.
 * @property edges [List] of [ApiEdge]s that are part of the graph.
 * @constructor Creates an [ApiGraph] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
public data class ApiGraph(
    val nodes: List<ApiNode>,
    val edges: List<ApiEdge>
)

/**
 * Attempts to create a domain [Graph] from an [ApiGraph] or returns an error if it is invalid.
 *
 * @receiver The source [ApiGraph].
 * @return [Result] containing either the created [Graph] or a [TranslationDTO] containing an error message.
 *
 * @author Jan Müller
 */
public fun ApiGraph.toDomainModel(): Result<Graph, TranslationDTO> = binding {
    val domainNodes = nodes.toDomainNodes().bind()
    val domainEdges = edges.toDomainEdges(domainNodes).bind()
    Graph(domainNodes, domainEdges)
}

/**
 * Attempts to transform a [List] of [ApiNode]s into domain [Node]s or returns an error any are invalid.
 *
 * @receiver The source [List] of [ApiNode]s.
 * @return [Result] containing either the transformed domain [Node]s or a [TranslationDTO] containing an error message.
 *
 * @author Jan Müller
 */
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

/**
 * Attempts to transform a [List] of [ApiEdge]s into domain [Edge]s or returns an error any are invalid.
 *
 * @receiver The source [List] of [ApiEdge]s.
 * @return [Result] containing either the transformed domain [ApiEdge]s or a [TranslationDTO] containing an error message.
 *
 * @author Jan Müller
 */
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

/**
 * Validates a [List] of relations and returns an error any are invalid.
 *
 * @receiver The [List] of relations.
 * @return [Result] containing either the [List] of relations or a [TranslationDTO] containing an error message.
 *
 * @author Jan Müller
 */
private fun List<String>.validatedRelations(): Result<List<String>, TranslationDTO> {
    forEach { relation ->
        if (!(relation.isNotBlank() && relation.first().isUpperCase())) {
            return@validatedRelations Err(TranslationDTO("api.error.invalid-relation", "relation" to relation))
        }
    }
    return Ok(this)
}

/**
 * Validates a [List] of functions and returns an error any are invalid.
 *
 * @receiver The [List] of functions.
 * @return [Result] containing either the [List] of functions or a [TranslationDTO] containing an error message.
 *
 * @author Jan Müller
 */
private fun List<String>.validatedFunctions(): Result<List<String>, TranslationDTO> {
    forEach { function ->
        if (!(function.isNotBlank() && function.first().isLowerCase())) {
            return@validatedFunctions Err(TranslationDTO("api.error.invalid-function", "function" to function))
        }
    }
    return Ok(this)
}

/**
 * Validates a [List] of constants and returns an error any are invalid.
 *
 * @receiver The [List] of constants.
 * @return [Result] containing either the [List] of constants or a [TranslationDTO] containing an error message.
 *
 * @author Jan Müller
 */
private fun List<String>.validatedConstants(): Result<List<String>, TranslationDTO> {
    forEach { constant ->
        if (!(constant.isNotBlank() && constant.first().isLowerCase())) {
            return@validatedConstants Err(TranslationDTO("api.error.invalid-constant", "constant" to constant))
        }
    }
    return Ok(this)
}
