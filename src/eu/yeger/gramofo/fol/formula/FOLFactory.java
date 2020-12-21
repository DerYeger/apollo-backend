package eu.yeger.gramofo.fol.formula;

import java.util.LinkedHashSet;


/**
 * This is a factory class to create FOLFormulas. Because all constructors are package private,
 * this is the only was to create them. This is to avoid misuse and to make it more comfortable.
 */
public class FOLFactory {

    ///////////////////// Constants /////////////////////////

    /**
     * Creates the constant True (tt). FOLConstant.TT is used as name.
     * @return a new instance of FOLConstant
     */
    public static FOLConstant createTrueConstant() {
        return new FOLConstant(FOLConstant.TT);
    }
    /**
     * Creates the constant False (ff). FOLConstant.FF is used as name.
     * @return a new instance of FOLConstant
     */
    public static FOLConstant createFalseConstant() {
        return new FOLConstant(FOLConstant.FF);
    }


    ///////////////////// Operands /////////////////////////

    /**
     * Creates an and-operator. FOLOperator.AND is used as name.
     * @return a new instance of FOLOperator
     */
    public static FOLOperator createOperatorAnd(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot) {
        return new FOLOperator(FOLType.And, leftOperand, rightOperand, hasBrackets, hasDot, FOLOperator.AND);
    }
    /**
     * Creates an or-operator. FOLOperator.OR is used as name.
     * @return a new instance of FOLOperator
     */
    public static FOLOperator createOperatorOr(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot) {
        return new FOLOperator(FOLType.Or, leftOperand, rightOperand, hasBrackets, hasDot, FOLOperator.OR);
    }
    /**
     * Creates an implication-operator. FOLOperator.IMPLICATION is used as name.
     * @return a new instance of FOLOperator
     */
    public static FOLOperator createOperatorImplication(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot) {

        return new FOLOperator(FOLType.Implication, leftOperand, rightOperand, hasBrackets, hasDot, FOLOperator.IMPLICATION);
    }
    /**
     * Creates a biimplication-operator. FOLOperator.BIIMPLICATION is used as name.
     * @return a new instance of FOLOperator
     */
    public static FOLOperator createOperatorBiimplication(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot) {
        return new FOLOperator(FOLType.Biimplication, leftOperand, rightOperand, hasBrackets, hasDot, FOLOperator.BIIMPLICATION);
    }
    /**
     * Creates a not-operator. FOLOperator.NOT is used as name.
     * @return a new instance of FOLOperator
     */
    public static FOLOperator createOperatorNot(FOLFormula operand, boolean hasBrackets, boolean hasDot) {
        return new FOLOperator(FOLType.Not, operand, hasBrackets, hasDot, FOLOperator.NOT);
    }


    ///////////////////// Function and Predicate /////////////////////////

    /**
     * Creates new postfix notated function. The name of the function is the symbol name.
     * @return a new instance of FOLFunction
     */
    public static FOLFunction createFunction(LinkedHashSet<? extends FOLFormula> children, boolean hasBrackets, boolean hasDot, String name) {
        return new FOLFunction(children, hasBrackets, hasDot, name);
    }
    /**
     * Creates new infix notated function. The name of the function is the symbol name.
     * @return a new instance of FOLFunction
     */
    public static FOLFunction createInfixFunction(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot, String name) {
        return new FOLFunction(leftOperand, rightOperand, hasBrackets, hasDot, name);
    }

    /**
     * Creates new postfix notated predicate. The name of the predicate is the symbol name.
     * @return a new instance of FOLPredicate
     */
    public static FOLPredicate createPredicate(LinkedHashSet<? extends FOLFormula> children, boolean hasBrackets, boolean hasDot, String name) {
        return new FOLPredicate(children, hasBrackets, hasDot, name);
    }

    /**
     * Creates new infix notated predicate. The name of the predicate is the symbol name.
     * @return a new instance of FOLPredicate
     */
    public static FOLPredicate createInfixPredicate(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot, String name) {
        return new FOLPredicate(leftOperand, rightOperand, hasBrackets, hasDot, name);
    }


    ////////////////// Quantifier and Variables ////////////////////////

    /**
     * Creates a new bound variable. This is only for variables, which are bound to a quantifier. For normal
     * variable use 0-ary functions instead.
     * @param name the name of the variable
     * @return a new instance of BoundVariable
     */
    public static FOLBoundVariable createBoundVariable(String name) {
        return new FOLBoundVariable(name);
    }


    public static FOLFormula createBoundVariable(String name, FOLFormula quantorSymbol) {
        return new FOLBoundVariable(name).withQuantorSymbol(quantorSymbol);
    }

    /**
     * Creates a new forall quantifier.
     * @param variable an instance of FOLBoundVariables. The name of this variable is used to bind other variables in
     *                 the formula subtree
     * @param operand the root of the subtree
     * @return a new instance of FOLQuantifier
     */
    public static FOLQuantifier createQuantifierForall(FOLBoundVariable variable, FOLFormula operand, boolean hasBrackets, boolean hasDot) {
        return new FOLQuantifier(FOLType.Forall, variable, operand, hasBrackets, hasDot, FOLQuantifier.FORALL);
    }

    /**
     * Creates a new exists quantifier.
     * @param variable an instance of FOLBoundVariables. The name of this variable is used to bind other variables in
     *                 the formula subtree
     * @param operand the root of the subtree
     * @return a new instance of FOLQuantifier
     */
    public static FOLQuantifier createQuantifierExists(FOLBoundVariable variable, FOLFormula operand, boolean hasBrackets, boolean hasDot) {
        return new FOLQuantifier(FOLType.Exists, variable, operand, hasBrackets, hasDot, FOLQuantifier.EXISTS);
    }
}
