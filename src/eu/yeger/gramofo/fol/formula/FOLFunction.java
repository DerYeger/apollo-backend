package eu.yeger.gramofo.fol.formula;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class FOLFunction extends FOLFormula {

    private Boolean isInfix;

    FOLFunction(LinkedHashSet<? extends FOLFormula> children, boolean hasBrackets, boolean hasDot, String name) {
        super(FOLType.Function, children, hasBrackets, hasDot, name);
        this.isInfix = false;
    }

    FOLFunction(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot, String name) {
        super(FOLType.Function, new LinkedHashSet<>(Arrays.asList(leftOperand, rightOperand)), hasBrackets, hasDot, name);
        this.isInfix = true;
    }

    @Override
    public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT;
        }
        StringBuilder sb = new StringBuilder();
        if (getIsInfix()) {
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth));
            sb.append(getName());
            sb.append(getChildAt(1).getFormulaStringForDepth(currentDepth + 1, maxDepth));
        } else {
            sb.append(getName());
            if (getChildren().size() > 0) {
                sb.append("(");
                sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth));
                getChildren().stream().skip(1).forEach(child -> sb.append(", ").append(child.getFormulaStringForDepth(currentDepth + 1, maxDepth)));
                sb.append(")");
            }
        }
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getIsInfix()) {
            sb.append(getChildAt(0));
            sb.append(getName());
            sb.append(getChildAt(1));
        } else {
            sb.append(getName());
            if (getChildren().size() > 0) {
                sb.append("(");
                sb.append(getChildAt(0));
                getChildren().stream().skip(1).forEach(child -> sb.append(", ").append(child));
                sb.append(")");
            }
        }
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }


    public boolean getIsInfix() {
        return isInfix;
    }

    public void setIsInfix(boolean isInfix) {
        this.isInfix = isInfix;
    }
}
