package eu.yeger.apollo.fol

import com.github.michaelbull.result.get
import eu.yeger.apollo.fol.parser.parseFormula
import eu.yeger.apollo.utils.shouldBe
import org.junit.jupiter.api.Test

class FOLTests {

    @Test
    fun testStringRepresentations() {
        mapOf(
            "tt" to "tt",
            "ff" to "ff",
            ". tt" to "tt",
            "A(x)" to "A(x)",
            "A(x,y)" to "A(x, y)",
            "x = y" to "x ≐ y",
            "exists x A(x)" to "∃x A(x)",
            "exists x !tt" to "∃x¬tt",
            "exists x. tt" to "∃x. tt",
            "exists x. tt || A(x)" to "∃x. tt ∨ A(x)",
            "exists x (tt || A(x))" to "∃x (tt ∨ A(x))",
            "exists x. (tt || A(x))" to "∃x. (tt ∨ A(x))",
            "(tt || ff)" to "tt ∨ ff",
            "(tt || ff) && tt" to "(tt ∨ ff) ∧ tt"
        ).forEach { (formula, expected) ->
            parseFormula(formula).get()?.formula.toString() shouldBe expected
        }
    }
}
