package eu.yeger.gramofo.fol.formula

/**
 * This is a factory class to create FOLFormulas. Because all constructors are package private,
 * this is the only was to create them. This is to avoid misuse and to make it more comfortable.
 */
object FOLFactory {

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
        return FOLPredicate(name = name, children = children, isInfix = false)
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
}
