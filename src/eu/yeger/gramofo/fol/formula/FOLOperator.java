package eu.yeger.gramofo.fol.formula;

import java.util.Arrays;
import java.util.LinkedHashSet;


public class FOLOperator extends FOLFormula {

    FOLOperator(FOLType type, FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot, String name) {
        super(type, new LinkedHashSet<>(Arrays.asList(leftOperand, rightOperand)), hasBrackets, hasDot, name);
    }

    FOLOperator(FOLType type, FOLFormula operand, boolean hasBrackets, boolean hasDot, String name) {
        super(type, new LinkedHashSet<>(Arrays.asList(operand)), hasBrackets, hasDot, name);
    }


    @Override
    public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT;
        }
        StringBuilder sb = new StringBuilder();
        if (getName().equals(NOT)) {
            sb.append(getName());
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth));
        } else {
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth));
            sb.append(" ");
            sb.append(getName());
            sb.append(" ");
            sb.append(getChildAt(1).getFormulaStringForDepth(currentDepth + 1, maxDepth));
        }
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getName().equals(NOT)) {
            sb.append(getName());
            sb.append(getChildAt(0));
        } else {
            sb.append(getChildAt(0));
            sb.append(" ");
            sb.append(getName());
            sb.append(" ");
            sb.append(getChildAt(1));
        }
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }
}
