package eu.yeger.gramofo.fol.formula

import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.FF
import eu.yeger.gramofo.fol.formula.FOLFormula.Companion.TT

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
    ): FOLOperator {
        return FOLOperator(
            type = FOLType.And,
            name = FOLFormula.AND,
            leftOperand = leftOperand,
            rightOperand = rightOperand
        )
    }

    /**
     * Creates an or-operator. FOLOperator.OR is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorOr(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ): FOLOperator {
        return FOLOperator(
            type = FOLType.Or,
            name = FOLFormula.OR,
            leftOperand = leftOperand,
            rightOperand = rightOperand
        )
    }

    /**
     * Creates an implication-operator. FOLOperator.IMPLICATION is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorImplication(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ): FOLOperator {
        return FOLOperator(
            type = FOLType.Implication,
            name = FOLFormula.IMPLICATION,
            leftOperand = leftOperand,
            rightOperand = rightOperand,
        )
    }

    /**
     * Creates a bi-implication-operator. FOLOperator.BI_IMPLICATION is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorBiImplication(
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ): FOLOperator {
        return FOLOperator(
            type = FOLType.BiImplication,
            name = FOLFormula.BI_IMPLICATION,
            leftOperand = leftOperand,
            rightOperand = rightOperand,
        )
    }

    /**
     * Creates a not-operator. FOLOperator.NOT is used as name.
     * @return a new instance of FOLOperator
     */
    fun createOperatorNot(operand: FOLFormula): FOLOperator {
        return FOLOperator(type = FOLType.Not, name = FOLFormula.NOT, operand = operand)
    }
    // /////////////////// Function and Predicate /////////////////////////
    /**
     * Creates new postfix notated function. The name of the function is the symbol name.
     * @return a new instance of FOLFunction
     */
    fun createFunction(
        name: String,
        children: Set<FOLFormula>
    ): FOLFunction {
        return FOLFunction(name = name, children = children)
    }

    /**
     * Creates new infix notated function. The name of the function is the symbol name.
     * @return a new instance of FOLFunction
     */
    fun createInfixFunction(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula
    ): FOLFunction {
        return FOLFunction(name = name, leftOperand = leftOperand, rightOperand = rightOperand)
    }

    /**
     * Creates new postfix notated predicate. The name of the predicate is the symbol name.
     * @return a new instance of FOLPredicate
     */
    fun createPredicate(
        name: String,
        children: Set<FOLFormula>,
    ): FOLPredicate {
        return FOLPredicate(name = name, children = children)
    }

    /**
     * Creates new infix notated predicate. The name of the predicate is the symbol name.
     * @return a new instance of FOLPredicate
     */
    fun createInfixPredicate(
        name: String,
        leftOperand: FOLFormula,
        rightOperand: FOLFormula,
    ): FOLPredicate {
        return FOLPredicate(name = name, leftOperand = leftOperand, rightOperand = rightOperand)
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
    ): FOLQuantifier {
        return FOLQuantifier(type = FOLType.ForAll, name = FOLFormula.FOR_ALL, variable = variable, operand = operand)
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
    ): FOLQuantifier {
        return FOLQuantifier(type = FOLType.Exists, name = FOLFormula.EXISTS, variable = variable, operand = operand)
    }
}
