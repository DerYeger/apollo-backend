package eu.yeger.gramofo.fol.formula;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class FOLPredicate extends FOLFormula {

    public static final HashMap<String, String> specialNames = new HashMap<>();

    {
        specialNames.put("=", "\u2250");
        specialNames.put("<=", "\u2264");
        specialNames.put(">=", "\u2265");
    }

    private Boolean isInfix = false;

    FOLPredicate(LinkedHashSet<? extends FOLFormula> children, boolean hasBrackets, boolean hasDot, String name) {
        super(FOLType.Predicate, children, hasBrackets, hasDot, name);
        this.isInfix = false;
    }

    FOLPredicate(FOLFormula leftOperand, FOLFormula rightOperand, boolean hasBrackets, boolean hasDot, String name) {
        super(FOLType.Predicate, new LinkedHashSet<>(Arrays.asList(leftOperand, rightOperand)), hasBrackets, hasDot, name);
        this.isInfix = true;
    }


    public boolean isEquality() {
        return INFIX_EQUALITY.equals(getName());
    }

    @Override
    public String getFormulaString() {
        StringBuilder sb = new StringBuilder();

        sb.append(specialNames.getOrDefault(getName(), getName()));
        sb.append("(");
        if (getChildren().size() > 0) {
            sb.append(getChildAt(0));
            getChildren().stream().skip(1).forEach(child -> sb.append(", ").append(child));
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT;
        }
        StringBuilder sb = new StringBuilder();
        if (getIsInfix()) {
            sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth));
            sb.append(specialNames.getOrDefault(getName(), getName()));
            sb.append(getChildAt(1).getFormulaStringForDepth(currentDepth + 1, maxDepth));
        } else {
            sb.append(specialNames.getOrDefault(getName(), getName()));
            sb.append("(");
            if (getChildren().size() > 0) {
                sb.append(getChildAt(0).getFormulaStringForDepth(currentDepth + 1, maxDepth));
                getChildren().stream().skip(1).forEach(child -> sb.append(", ").append(child.getFormulaStringForDepth(currentDepth + 1, maxDepth)));
            }
            sb.append(")");
        }
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getIsInfix()) {
            sb.append(getChildAt(0));
            sb.append(specialNames.getOrDefault(getName(), getName()));
            sb.append(getChildAt(1));
        } else {
            sb.append(specialNames.getOrDefault(getName(), getName()));
            sb.append("(");
            if (getChildren().size() > 0) {
                sb.append(getChildAt(0));
                getChildren().stream().skip(1).forEach(child -> sb.append(", ").append(child));
            }
            sb.append(")");
        }
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }


    public boolean getIsInfix() {
        return isInfix;
    }

    public Boolean isInfixProperty() {
        return isInfix;
    }

    public void setIsInfix(boolean isInfix) {
        this.isInfix = isInfix;
    }
}
