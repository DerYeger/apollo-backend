package eu.yeger.gramofo.fol

import eu.yeger.gramofo.utils.shouldNotBe
import org.junit.jupiter.api.Test

class ParserTests {

    @Test
    fun `verify that parsing constants works`() {
        FOLParser().parseFormula("a = b").result shouldNotBe null
    }

    @Test
    fun `verify that parsing functions works`() {
        FOLParser().parseFormula("f(x) = y").result shouldNotBe null
    }

    @Test
    fun `verify that parsing predicates works`() {
        FOLParser().parseFormula("A(x)").result shouldNotBe null
    }

    @Test
    fun `verify that parsing negations works`() {
        FOLParser().parseFormula("!A()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing junction works`() {
        FOLParser().parseFormula("A() && B()").result shouldNotBe null
        FOLParser().parseFormula("A() & B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing disjunction works`() {
        FOLParser().parseFormula("A() || B()").result shouldNotBe null
        FOLParser().parseFormula("A() | B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing implication works`() {
        FOLParser().parseFormula("A() -> B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing bi-implication works`() {
        FOLParser().parseFormula("A() <-> B()").result shouldNotBe null
    }

    @Test
    fun `verify that parsing existential quantifier works`() {
        FOLParser().parseFormula("exists x. A(x)").result shouldNotBe null
    }

    @Test
    fun `verify that parsing universal quantifier works`() {
        FOLParser().parseFormula("forall x. A(x)").result shouldNotBe null
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
        formulas.forEach { formula ->
            FOLParser().parseFormula(formula).errorMessage shouldNotBe null
        }
    }
}
