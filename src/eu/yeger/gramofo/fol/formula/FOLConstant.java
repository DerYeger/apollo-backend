package eu.yeger.gramofo.fol.formula;

public class FOLConstant extends FOLFormula {

    FOLConstant(String name) {
        super(FOLType.Constant, null, false, false, name);
    }

    @Override
    public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth && maxDepth != -1) {
            return DOT;
        }
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
