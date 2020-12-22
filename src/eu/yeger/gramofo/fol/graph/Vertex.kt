package eu.yeger.gramofo.fol.graph

class Vertex(
    val readableName: String,
    val relations: List<String>,
    val constants: List<String>
) {
    val stringAttachments = relations + constants
}
