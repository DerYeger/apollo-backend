package eu.yeger.gramofo.fol

import com.github.michaelbull.result.getOrElse
import eu.yeger.gramofo.fol.graph.*
import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.utils.shouldBe
import eu.yeger.gramofo.utils.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class FOLTests {
    @Test
    fun testFOLParser() {
        val result = parseFormula("exists x. exists y. f(x) = y")
        result shouldNotBe null

        val a = Node("a", listOf("A"), listOf())
        val b = Node("b", listOf(), listOf())

        val aToB = Edge(a, b, listOf(), listOf("f"))
        val bToB = Edge(b, b, listOf(), listOf("f"))

        val graph = Graph(listOf(a, b), listOf(aToB, bToB))
        val modelResult = checkModel(graph, result.result!!).getOrElse { fail("Input should have been valid.") }
        println(modelResult)
        modelResult.isModel shouldBe true
    }

    @Test
    fun testNegativeFOLParser() {
        val result = parseFormula("exists x. exists y. f(x) = y && !(x = y)")
        val a = Node("a", listOf("A"), listOf())
        val b = Node("b", listOf(), listOf())

        val aToA = Edge(a, a, listOf(), listOf("f"))
        val bToB = Edge(b, b, listOf(), listOf("f"))

        val graph = Graph(listOf(a, b), listOf(aToA, bToB))
        val modelResult = checkModel(graph, result.result!!).getOrElse { fail("Input should have been valid.") }
        println(modelResult)
        modelResult.isModel shouldBe false
    }
}
