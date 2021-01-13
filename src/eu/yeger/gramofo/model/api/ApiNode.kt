package eu.yeger.gramofo.model.api

import kotlinx.serialization.Serializable

/**
 * Represents a node of an [ApiGraph].
 *
 * @property name The name of the node.
 * @property relations [List] of relation symbols. Each must start with an uppercase letter.
 * @property constants [List] of constant symbols. Each must start with a lowercase letter.
 * @constructor Creates an [ApiNode] with the given parameters.
 *
 * @author Jan Müller
 */
@Serializable
data class ApiNode(
    val name: String,
    val relations: List<String>,
    val constants: List<String>
)
