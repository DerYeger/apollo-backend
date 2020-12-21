package eu.yeger.gramofo.fol.formula;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class FOLQuantifier extends FOLFormula {

    FOLQuantifier(FOLType type, FOLBoundVariable variable, FOLFormula operand, boolean hasBrackets, boolean hasDot, String name) {
        super(type, new LinkedHashSet<>(Arrays.asList(variable, operand)), hasBrackets, hasDot, name);
    }

    ///TODO
    @Override
    public String getFormulaString() {

        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(getChildAt(0));

        return sb.toString();
    }

    @Override
    public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT;
        }
        FOLFormula child0 = getChildAt(0);
        FOLFormula child1 = getChildAt(1);
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(child0);
        if (!child1.getHasDot() && !isUnary(child1)) {
            sb.append(" ");
        }
        sb.append(child1.getFormulaStringForDepth(currentDepth, maxDepth));
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        FOLFormula child0 = getChildAt(0);
        FOLFormula child1 = getChildAt(1);
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(child0);
        if (!child1.getHasDot() && !isUnary(child1)) {
            sb.append(" ");
        }
        sb.append(child1);
        maybeWrapBracketsAndDot(sb);
        return sb.toString();
    }

    public boolean isUnary(FOLFormula formula) {
        FOLType type = formula.getType();
        return Arrays.asList(FOLType.Exists, FOLType.Forall, FOLType.Not).stream().anyMatch(forbiddenType -> forbiddenType == type);
    }

}
