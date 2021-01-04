package eu.yeger.gramofo.model.domain

data class Edge(
    val source: Node,
    val target: Node,
    val relations: List<String>,
    val functions: List<String>
)
