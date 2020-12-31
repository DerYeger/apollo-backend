package eu.yeger.gramofo.model.domain

data class Node(
    val name: String,
    val relations: List<String>,
    val constants: List<String>
) {
    val stringAttachments = relations + constants
}
