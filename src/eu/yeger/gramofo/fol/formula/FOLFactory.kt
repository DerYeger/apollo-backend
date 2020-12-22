package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.FF
import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.TT
import java.util.*

/**
 * This is a factory class to create FOLFormulas. Because all constructors are package private,
 * this is the only was to create them. This is to avoid misuse and to make it more comfortable.
 */
object FOLFactory {
    // /////////////////// Constants /////////////////////////
    /**
     * Creates the constant True (tt). FOLConstant.TT is used as name.
     * @return a new instance of FOLConstant
     */
    fun createTrueConstant(): FOLConstant {
        return FOLConstant(TT)
    }

    /**
     * Creates the constant False (ff). FOLConstant.FF is used as name.
     * @return a new instance of FOLConstant
     */
    fun createFalseConstant(): FOLConstant {
        return FOLConstant(FF)
    }
    // /////////////////// Operands /////////////////////////
    /**
     * Creates an and-operator. FOLOperator.AND is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorAnd(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean
    ): FOLOperator {
        return FOLOperator(FOLType.And, leftOperand, rightOperand, hasBrackets, hasDot, FOLFormula.AND)
    }

    /**
     * Creates an or-operator. FOLOperator.OR is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorOr(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean
    ): FOLOperator {
        return FOLOperator(FOLType.Or, leftOperand, rightOperand, hasBrackets, hasDot, FOLFormula.OR)
    }

    /**
     * Creates an implication-operator. FOLOperator.IMPLICATION is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorImplication(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean
    ): FOLOperator {
        return FOLOperator(FOLType.Implication, leftOperand, rightOperand, hasBrackets, hasDot, FOLFormula.IMPLICATION)
    }

    /**
     * Creates a bi-implication-operator. FOLOperator.BI_IMPLICATION is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorBiImplication(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean
    ): FOLOperator {
        return FOLOperator(
            FOLType.BiImplication,
            leftOperand,
            rightOperand,
            hasBrackets,
            hasDot,
            FOLFormula.BI_IMPLICATION
        )
    }

    /**
     * Creates a not-operator. FOLOperator.NOT is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorNot(operand: FOLFormula, hasBrackets: Boolean, hasDot: Boolean): FOLOperator {
        return FOLOperator(FOLType.Not, operand, hasBrackets, hasDot, FOLFormula.NOT)
    }
    // /////////////////// Function and Predicate /////////////////////////
    /**
     * Creates new postfix notated function. The name of the function is the symbol name.
     * @return a new instance of FOLFunction
     */
    fun createFunction(
        children: LinkedHashSet<out FOLFormula>,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ): FOLFunction {
        return FOLFunction(children, hasBrackets, hasDot, name)
    }

    /**
     * Creates new infix notated function. The name of the function is the symbol name.
     * @return a new instance of FOLFunction
     */
    fun createInfixFunction(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ): FOLFunction {
        return FOLFunction(leftOperand, rightOperand, hasBrackets, hasDot, name)
    }

    /**
     * Creates new postfix notated predicate. The name of the predicate is the symbol name.
     * @return a new instance of FOLPredicate
     */
    fun createPredicate(
        children: LinkedHashSet<out FOLFormula>,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ): FOLPredicate {
        return FOLPredicate(children, hasBrackets, hasDot, name)
    }

    /**
     * Creates new infix notated predicate. The name of the predicate is the symbol name.
     * @return a new instance of FOLPredicate
     */
    fun createInfixPredicate(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean,
        name: String
    ): FOLPredicate {
        return FOLPredicate(leftOperand, rightOperand, hasBrackets, hasDot, name)
    }
    // //////////////// Quantifier and Variables ////////////////////////
    /**
     * Creates a new bound variable. This is only for variables, which are bound to a quantifier. For normal
     * variable use 0-ary functions instead.
     * @param name the name of the variable
     * @return a new instance of BoundVariable
     */
    fun createBoundVariable(name: String): FOLBoundVariable {
        return FOLBoundVariable(name)
    }

    fun createBoundVariable(name: String, quantorSymbol: FOLFormula): FOLFormula {
        return FOLBoundVariable(name).withQuantorSymbol(quantorSymbol)
    }

    /**
     * Creates a new forAll quantifier.
     * @param variable an instance of FOLBoundVariables. The name of this variable is used to bind other variables in
     * the formula subtree
     * @param operand the root of the subtree
     * @return a new instance of FOLQuantifier
     */
    fun createQuantifierForAll(
        variable: FOLBoundVariable,
        operand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean
    ): FOLQuantifier {
        return FOLQuantifier(FOLType.ForAll, variable, operand, hasBrackets, hasDot, FOLFormula.FOR_ALL)
    }

    /**
     * Creates a new exists quantifier.
     * @param variable an instance of FOLBoundVariables. The name of this variable is used to bind other variables in
     * the formula subtree
     * @param operand the root of the subtree
     * @return a new instance of FOLQuantifier
     */
    fun createQuantifierExists(
        variable: FOLBoundVariable,
        operand: FOLFormula,
        hasBrackets: Boolean,
        hasDot: Boolean
    ): FOLQuantifier {
        return FOLQuantifier(FOLType.Exists, variable, operand, hasBrackets, hasDot, FOLFormula.EXISTS)
    }
}
