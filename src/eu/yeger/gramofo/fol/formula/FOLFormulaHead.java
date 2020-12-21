package eu.yeger.gramofo.fol.formula;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is used to store meta data for a FOLFormula.
 */
public class FOLFormulaHead {

    private FOLFormula formula;
    private Map<String, String> symbolTable;

    public FOLFormulaHead(FOLFormula formula, Map<String, String> symbolTable) {
        formula.countVariables();
        this.formula     = formula;
        this.symbolTable = new HashMap<>(symbolTable);
    }


    /////////////////////////// getter and setter //////////////////////////////

    public FOLFormula getFormula() {
        return formula;
    }
    public void setFormula(FOLFormula formula) {
        this.formula = formula;
    }

    public Map<String, String> getSymbolTable() {
        return symbolTable;
    }
    public void setSymbolTable(Map<String, String> symbolTable) {
        this.symbolTable = symbolTable;
    }
}
