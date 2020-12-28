package eu.yeger.gramofo.fol.graph

data class Edge(
    val source: Node,
    val target: Node,
    val relations: List<String>,
    val functions: List<String>
) {
    val stringAttachments = relations + functions
}
