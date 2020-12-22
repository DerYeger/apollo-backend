package eu.yeger.gramofo.fol.graph

data class Edge(
    val source: Vertex,
    val target: Vertex,
    val relations: List<String>,
    val functions: List<String>
) {
    val stringAttachments = relations + functions
}
