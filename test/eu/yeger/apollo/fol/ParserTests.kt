package eu.yeger.apollo.fol

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import eu.yeger.apollo.fol.parser.parseFormula
import eu.yeger.apollo.utils.shouldNotBe
import org.junit.jupiter.api.Test

class ParserTests {

    @Test
    fun `verify that parsing constants works`() {
        parseFormula("a = b").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing functions works`() {
        parseFormula("f(x) = y").get() shouldNotBe null
        parseFormula("f(g(x)) = y").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing predicates works`() {
        parseFormula("A(x)").get() shouldNotBe null
        parseFormula("A(x,y)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing negations works`() {
        parseFormula("!A(x)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing junction works`() {
        parseFormula("A(x) && B(x)").get() shouldNotBe null
        parseFormula("A(x) & B(x)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing disjunction works`() {
        parseFormula("A(x) || B(x)").get() shouldNotBe null
        parseFormula("A(x) | B(x)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing implication works`() {
        parseFormula("A(x) -> B(x)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing bi-implication works`() {
        parseFormula("A(x) <-> B(x)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing existential quantifier works`() {
        parseFormula("exists x. A(x)").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing universal quantifier works`() {
        parseFormula("forall x. A(x)").get() shouldNotBe null
        parseFormula("forall x. forall y. A(x, y)").get() shouldNotBe null
        parseFormula("(forall x. A(x)) && (exists x. B(x))").get() shouldNotBe null
    }

    @Test
    fun `verify that parsing nested formulas works`() {
        parseFormula("forall x. exists y. forall z. f(x) = y && R(y,z) && R(z,y) || (f(x) = x -> f(z) = a <-> ff)").get() shouldNotBe null
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
        formulas.forEach { formula -> parseFormula(formula).getError() shouldNotBe null }
    }
}
