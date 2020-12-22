package eu.yeger.gramofo.fol

import eu.yeger.gramofo.fol.graph.Edge
import eu.yeger.gramofo.fol.graph.Graph
import eu.yeger.gramofo.fol.graph.Vertex
import eu.yeger.gramofo.fol.graph.checkModel
import eu.yeger.gramofo.utils.shouldBe
import eu.yeger.gramofo.utils.shouldNotBe
import org.junit.jupiter.api.Test

class FOLTests {
    @Test
    fun testFOLParser() {
        val result = parseFormula("exists x. exists y. f(x) = y")
        result shouldNotBe null

        val a = Vertex("a", listOf("A"), listOf())
        val b = Vertex("b", listOf(), listOf())

        val aToB = Edge(a, b, listOf(), listOf("f"))
        val bToB = Edge(b, b, listOf(), listOf("f"))

        val graph = Graph(listOf(a, b), listOf(aToB, bToB))
        val modelResult = checkModel(graph, result.result!!)
        modelResult shouldBe null
    }

    @Test
    fun testNegativeFOLParser() {
        val result = parseFormula("exists x. exists y. f(x) = y && !(x = y)")
        val a = Vertex("a", listOf("A"), listOf())
        val b = Vertex("b", listOf(), listOf())

        val aToA = Edge(a, a, listOf(), listOf("f"))
        val bToB = Edge(b, b, listOf(), listOf("f"))

        val graph = Graph(listOf(a, b), listOf(aToA, bToB))
        val modelResult = checkModel(graph, result.result!!)
        modelResult shouldBe ""
    }
}
