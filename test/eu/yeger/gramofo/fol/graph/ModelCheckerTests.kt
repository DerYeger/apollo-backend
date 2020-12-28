package eu.yeger.gramofo.fol.graph

import com.github.michaelbull.result.get
import eu.yeger.gramofo.fol.parseFormula
import eu.yeger.gramofo.utils.shouldBe
import org.junit.jupiter.api.Test

class ModelCheckerTests {

    @Test
    fun `verify that checking constants works`() {
        val graph = Graph(listOf(), listOf())
        listOf(
            "tt" to true,
            "ff" to false
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking negation works`() {
        val graph = Graph(listOf(), listOf())
        listOf(
            "!tt" to false,
            "!ff" to true
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking conjunctions works`() {
        val graph = Graph(listOf(), listOf())
        listOf(
            "tt && tt" to true,
            "tt && ff" to false,
            "ff && tt" to false,
            "ff && ff" to false,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking disjunction works`() {
        val graph = Graph(listOf(), listOf())
        listOf(
            "tt || tt" to true,
            "tt || ff" to true,
            "ff || tt" to true,
            "ff || ff" to false,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking implication works`() {
        val graph = Graph(listOf(), listOf())
        listOf(
            "tt -> tt" to true,
            "tt -> ff" to false,
            "ff -> tt" to true,
            "ff -> ff" to true,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking bi-implication works`() {
        val graph = Graph(listOf(), listOf())
        listOf(
            "tt <-> tt" to true,
            "tt <-> ff" to false,
            "ff <-> tt" to false,
            "ff <-> ff" to true,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking unary relations works`() {
        val graph = Graph(
            listOf(
                Node("0", listOf("R"), listOf("a")),
                Node("1", listOf(), listOf("b")),
            ),
            listOf()
        )
        listOf(
            "R(a)" to true,
            "R(b)" to false,
            "U(a)" to false,
            "U(b)" to false,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking binary relations works`() {
        val nodes = listOf(
            Node("0", listOf(), listOf("a")),
            Node("1", listOf(), listOf("b")),
        )
        val edges = listOf(
            Edge(nodes[0], nodes[1], listOf("R"), listOf())
        )
        val graph = Graph(nodes, edges)
        listOf(
            "R(a, b)" to true,
            "R(b, a)" to false,
            "U(a, b)" to false,
            "U(b, a)" to false,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking equality works`() {
        val nodes = listOf(
            Node("0", listOf(), listOf("a")),
            Node("1", listOf(), listOf("b")),
        )
        val edges = listOf(
            Edge(nodes[0], nodes[1], listOf(), listOf("f")),
            Edge(nodes[1], nodes[1], listOf(), listOf("f"))
        )
        val graph = Graph(nodes, edges)
        listOf(
            "f(a)=b" to true,
            "f(b)=b" to true,
            "f(a)=a" to false,
            "f(b)=a" to false,
            "f(f(a))=b" to true,
            "f(f(b))=b" to true,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking existential quantor works`() {
        val nodes = listOf(
            Node("0", listOf(), listOf("a")),
            Node("1", listOf(), listOf("b")),
        )
        val edges = listOf(
            Edge(nodes[0], nodes[1], listOf(), listOf("f")),
            Edge(nodes[1], nodes[1], listOf(), listOf("f"))
        )
        val graph = Graph(nodes, edges)
        listOf(
            "exists x. f(x)=x" to true,
            "exists x. f(x)=b" to true,
            "exists x. f(x)=a" to false,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }

    @Test
    fun `verify that checking universal quantor works`() {
        val nodes = listOf(
            Node("0", listOf(), listOf("a")),
            Node("1", listOf(), listOf("b")),
        )
        val edges = listOf(
            Edge(nodes[0], nodes[1], listOf(), listOf("f")),
            Edge(nodes[1], nodes[1], listOf(), listOf("f"))
        )
        val graph = Graph(nodes, edges)
        listOf(
            "forall x. f(x)=b" to true,
            "forall x. f(x)=a" to false,
            "forall x. f(a)=x" to false,
            "forall x. f(b)=x" to false,
            "forall x f(x)=b && forall x !(f(x)=a) " to true,
        ).forEach { (formula, expectedResult) ->
            checkModel(graph, parseFormula(formula).result!!).get()?.isModel shouldBe expectedResult
        }
    }
}
