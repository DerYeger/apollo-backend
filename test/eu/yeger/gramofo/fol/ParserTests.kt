package eu.yeger.gramofo.fol

import eu.yeger.gramofo.fol.parser.parseFormula
import eu.yeger.gramofo.utils.shouldNotBe
import org.junit.jupiter.api.Test

class ParserTests {

    @Test
    fun `verify that parsing constants works`() {
        parseFormula("a = b").result shouldNotBe null
    }

    @Test
    fun `verify that parsing functions works`() {
        parseFormula("f(x) = y").result shouldNotBe null
        parseFormula("f(x, z) = y").result shouldNotBe null
    }

    @Test
    fun `verify that parsing predicates works`() {
        parseFormula("A()").result shouldNotBe null
        parseFormula("A(x)").result shouldNotBe null
        parseFormula("A(x,y)").result shouldNotBe null
    }

    @Test
    fun `verify that parsing negations works`() {
        parseFormula("!A()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing junction works`() {
        parseFormula("A() && B()").result shouldNotBe null
        parseFormula("A() & B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing disjunction works`() {
        parseFormula("A() || B()").result shouldNotBe null
        parseFormula("A() | B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing implication works`() {
        parseFormula("A() -> B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing bi-implication works`() {
        parseFormula("A() <-> B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing existential quantifier works`() {
        parseFormula("exists x. A(x)").result shouldNotBe null
    }

    @Test
    fun `verify that parsing universal quantifier works`() {
        parseFormula("forall x. A(x)").result shouldNotBe null
        parseFormula("forall x. forall y. A(x, y)").result shouldNotBe null
        parseFormula("(forall x. A(x)) && (exists x. B(x))").result shouldNotBe null
    }

    @Test
    fun `verify that parsing nested formulas works`() {
        parseFormula("forall x. exists y. forall z. f(x) = y && R(y,z) && R(z,y) || (f(x) = x -> f(z) = a <-> ff)").result shouldNotBe null
    }

    @Test
    fun `verify that invalid formulas result in errors`() {
        val formulas = listOf(
            "A(A)",
            "tt)",
            "exists . A(x)",
            "exists B. A(B)",
            "exists x. exists x. x == x",
            "(tt",
            "(tt &&",
            "y = f(x,y"
        )
        formulas.forEach { formula -> parseFormula(formula).errorMessage shouldNotBe null }
    }
}
