package eu.yeger.gramofo.fol

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import eu.yeger.gramofo.fol.graph.*
import eu.yeger.gramofo.utils.shouldBeInstanceOf
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
        println(modelResult)
        modelResult shouldBeInstanceOf Ok::class.java
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
        println(modelResult)
        modelResult shouldBeInstanceOf Err::class.java
    }
}
