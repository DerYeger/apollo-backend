package eu.yeger.gramofo.fol.formula;

import java.util.LinkedHashSet;
import java.util.Set;

public class FOLSequence  {

    private Set<FOLFormulaHead> antecedent;
    private Set<FOLFormulaHead> succedent;
    private Set<FOLSequence> subsequences;
    private FOLSequence parentSequence;

    public FOLSequence() {
        this.antecedent     = new LinkedHashSet<>();
        this.succedent      = new LinkedHashSet<>();
        this.subsequences   = new LinkedHashSet<>();
        this.parentSequence = null;

        addReferentialIntegrity();
    }

    private void addReferentialIntegrity() {
//        parentSequence.addListener((observable, oldValue, newValue) -> {
//            if (oldValue != null) {
//                oldValue.getSubsequences().remove(this);
//            }
//            if (newValue != null) {
//                newValue.getSubsequences().add(this);
//            }
//        });
//
//        subsequences.addListener((SetChangeListener<FOLSequence>) change -> {
//            if (change.wasRemoved()) {
//                change.getElementRemoved().setParentSequence(null);
//            }
//            if (change.wasAdded()) {
//                change.getElementAdded().setParentSequence(this);
//            }
//        });
    }


    public Set<FOLFormulaHead> getAntecedent() {
        return antecedent;
    }
    public void setAntecedent(LinkedHashSet<FOLFormulaHead> antecedent) {
        this.antecedent = antecedent;
    }



    public Set<FOLFormulaHead> getSuccedent() {
        return succedent;
    }
    public void setSuccedent(LinkedHashSet<FOLFormulaHead> succedent) {
        this.succedent = succedent;
    }



    public Set<FOLSequence> getSubsequences() {
        return subsequences;
    }
    public void setSubsequences(Set<FOLSequence> subsequences) {
        this.subsequences = subsequences;
    }



    public FOLSequence getParentSequence() {
        return parentSequence;
    }
    public void setParentSequence(FOLSequence parentSequence) {
        this.parentSequence = parentSequence;
    }
}
