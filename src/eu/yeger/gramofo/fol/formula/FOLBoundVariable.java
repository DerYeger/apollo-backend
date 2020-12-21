package eu.yeger.gramofo.fol.formula;

import java.util.LinkedHashSet;
import java.util.UUID;


public class FOLBoundVariable extends FOLFormula {

    private UUID uuid;
    private UUID quantorSymbol;

    FOLBoundVariable(String name) {
        super(FOLType.Variable, new LinkedHashSet<>(), false, false, name);
        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    public FOLBoundVariable withQuantorSymbol(FOLFormula quantorSymbol) {
        if (quantorSymbol instanceof FOLBoundVariable) {
            this.quantorSymbol = ((FOLBoundVariable) quantorSymbol).getUuid();
        }
        return this;
    }

    public UUID getQuantorSymbolUuid() {
        return quantorSymbol;
    }

    public UUID getUuid() {
        return uuid;
    }
}
