package eu.yeger.gramofo.fol

import eu.yeger.gramofo.fol.graph.Edge
import eu.yeger.gramofo.fol.graph.Graph
import eu.yeger.gramofo.fol.graph.ModelChecker
import eu.yeger.gramofo.fol.graph.Vertex
import eu.yeger.gramofo.utils.shouldBe
import eu.yeger.gramofo.utils.shouldNotBe
import org.junit.jupiter.api.Test

class FOLTests {
    @Test
    fun testFOLParser() {
        val result = FOLParser().parseFormula("exists x. exists y. f(x) = y")
        result shouldNotBe null

        val graph = Graph()

        val a = Vertex()
        a.readableName = "a"
        a.stringAttachments = listOf("A")

        val b = Vertex()
        b.readableName = "b"

        val aToB = Edge()
        aToB.fromVertex = a
        aToB.toVertex = b
        aToB.stringAttachments = listOf("f")

        val bToB = Edge()
        bToB.fromVertex = b
        bToB.toVertex = b
        bToB.stringAttachments = listOf("f")

        graph.vertices
            .addAll(listOf(a, b))
        graph.edges
            .addAll(listOf(aToB, bToB))

        val modelResult = ModelChecker().checkModel(graph, result.result!!)
        modelResult shouldBe true
    }

    @Test
    fun testNegativeFOLParser() {
        val result = FOLParser().parseFormula("exists x. exists y. f(x) = y && !(x = y)")

        val graph = Graph()

        val a = Vertex()
        a.readableName = "a"
        a.stringAttachments = listOf("A")

        val b = Vertex()
        b.readableName = "b"

        val aToA = Edge()
        aToA.fromVertex = a
        aToA.toVertex = a
        aToA.stringAttachments = listOf("f")

        val bToB = Edge()
        bToB.fromVertex = b
        bToB.toVertex = b
        bToB.stringAttachments = listOf("f")

        graph.vertices
            .addAll(listOf(a, b))
        graph.edges
            .addAll(listOf(aToA, bToB))

        val modelResult = ModelChecker().checkModel(graph, result.result!!)
        modelResult shouldBe false
    }
}
