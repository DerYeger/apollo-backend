package eu.yeger.apollo.model.api

import kotlinx.serialization.Serializable

/**
 * Represents an edge between to nodes of an [ApiGraph].
 *
 * @property source The name of the source node.
 * @property target The name of the target node.
 * @property relations [List] of relation symbols. Each must start with an uppercase letter.
 * @property functions [List] of function symbols. Each must start with a lowercase letter.
 * @constructor Creates an [ApiEdge] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
public data class ApiEdge(
  val source: String,
  val target: String,
  val relations: List<String>,
  val functions: List<String>
)
