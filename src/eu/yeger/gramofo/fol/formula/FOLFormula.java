package eu.yeger.gramofo.fol.formula;

import java.util.*;

/**
 * This is the super class of all formula types.
 */
public abstract class FOLFormula {

    public static final String DOT = "...";

    public static final String TT = "tt";
    public static final String FF = "ff";

    public static final String NOT = "\u00AC";
    public static final String AND = "\u2227";
    public static final String OR = "\u2228";
    public static final String IMPLICATION = "\u2192";
    public static final String BIIMPLICATION = "\u2194";
    public static final List<String> LOGICAL_OPERANDS = Arrays.asList(NOT, AND, OR, IMPLICATION, BIIMPLICATION);

    public static final String EXISTS = "\u2203";
    public static final String FORALL = "\u2200";
    public static final List<String> QUANTIFIERS = Arrays.asList(EXISTS, FORALL);

    public static final String INFIX_EQUALITY = "="; // equal sign with a dot on top

    public static final FOLFormula DUMMY = new FOLFormula(null, null, false, false, "?") {
        @Override
        public String getFormulaStringForDepth(int currentDepth, int maxDepth) {
            return getName();
        }

        @Override
        public String toString() {
            return getName();
        }
    };

    private FOLType type;
    private FOLFormula parent;
    private Set<FOLFormula> children;
    private Boolean hasBrackets;
    private Boolean hasDot;
    private String name;

    private ArrayList<FOLBoundVariable> variables;

    /**
     * @param type the type of the formula chosen from enum FOLType
     * @param children the children of the formula
     * @param hasBrackets true if the formula should be wrapped with brackets
     * @param hasDot true if the formula should have a dot
     * @param name the name of the formula
     */
    public FOLFormula(FOLType type, LinkedHashSet<? extends FOLFormula> children, boolean hasBrackets, boolean hasDot, String name) {
        children = children == null ? new LinkedHashSet<>() : children;

        this.type         = type;
        this.parent       = null;
        this.children     = new LinkedHashSet<>();
        this.hasBrackets  = hasBrackets;
        this.hasDot       = hasDot;
        this.name         = name;

        addReferentialIntegrity();
        this.children.addAll(children);
    }

    private void addReferentialIntegrity() {

//        parent.addListener( (observable, oldValue, newValue ) -> {
//            if (oldValue != null){
//                oldValue.getChildren().remove(this);
//            }
//            if (newValue != null){
//                newValue.getChildren().add(this);
//            }
//        });
//
//        children.addListener( (SetChangeListener<FOLFormula>) change -> {
//            if(change.wasRemoved()){
//                change.getElementRemoved().setParent(null);
//            }
//            if(change.wasAdded()){
//                change.getElementAdded().setParent(this);
//            }
//        });

    }

    //////////////////// operation and information //////////////////////

    public String getFormulaString() {
        return getName();
    }

    public abstract String getFormulaStringForDepth(int currentDepth, int maxDepth);

    @Override
    public abstract String toString();

    /**
     * @return the child at the specified position of this FOLFormula or a DUMMY, if there is no child at this position.
     */
    public FOLFormula getChildAt(int position) {
        ArrayList<FOLFormula> list = new ArrayList<>(children);
        if(list.size() > position ) {
            return list.get(position);
        } else {
            return DUMMY;
        }
    }

    /**
     * If this formula has brackets and/or a dot it will be added to the StringBuilder
     * @param sb a StringBuilder in which to add brackets and and dot
     * @return the given StringBuilder sb
     */
    public StringBuilder maybeWrapBracketsAndDot(StringBuilder sb) {
        sb.insert(0, getHasBrackets() ? "(" : "");
        sb.insert(0, getHasDot() ? ". " : "");
        sb.append(getHasBrackets() ? ")" : "");
        return sb;
    }

    public void dump() {
        for (int i = 0; i < getChildren().size(); i++) {
            System.out.println(getChildAt(i));
            getChildAt(i).dump();
        }
    }

    private ArrayList<FOLBoundVariable> variables() {
        ArrayList<FOLBoundVariable> list = new ArrayList<>();

        for (int i = 0; i < getChildren().size(); i++) {
            FOLFormula child = getChildAt(i);

            if (this instanceof FOLQuantifier && child instanceof FOLBoundVariable) {
                list.add((FOLBoundVariable) child);
            }

            list.addAll(child.variables());

        }
        return list;
    }

    public boolean containsImplication() {

        if (getType().equals(FOLType.Implication) || getType().equals(FOLType.Biimplication)) {
            return true;
        } else if (getChildren() != null && !getChildren().isEmpty()){
            for (FOLFormula folFormula : getChildren()) {
                if (folFormula.containsImplication()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void countVariables() {
        // Remove duplicates
//        this.variables = new ArrayList<>(new LinkedHashSet<>(variables()));


        this.variables = variables();
    }


    //////////////////// getter und setter //////////////////////



    public FOLType getType() {
        return type;
    }
    /**
     * Specifies the type of the formula. This should match with the corresponding subclass.
     * @return .
     */
    public void setType(FOLType type) {
        this.type = type;
    }




    public FOLFormula getParent() {
        return parent;
    }
    /**
     * The parent is set automatically, if one formula ist set as child of some parent formula.
     * It indicates, that this formula is a child of the formula, which is assigned to parent.
     * @return .
     */
    public void setParent(FOLFormula parent) {
        this.parent = parent;
    }



    public Set<FOLFormula> getChildren() {
        return children;
    }

    public Set<FOLFormula> getClearChildren() {

        ArrayList<FOLFormula> toRemove = new ArrayList<>();

        if (this instanceof FOLPredicate) {
            return new LinkedHashSet<>();
        }

        for (FOLFormula folFormula : getChildren()) {
            if (this instanceof FOLQuantifier && folFormula instanceof FOLBoundVariable) {
                toRemove.add(folFormula);
            }
        }

        Set<FOLFormula> children = getChildren();
        children.removeAll(toRemove);
        return children;
    }

    /**
     * This is a list of sub formulas. Every sub class could use this to store sub formulas (and, or, implication,
     * forall, exists, ...) or leave it empty ( variables, 0-ary functions, 0-ary predicates, ...)
     * @return -
     */
    public Set<FOLFormula> childrenProperty() {
        return children;
    }
    public void setChildren(Set<FOLFormula> children) {
        this.children = children;
    }




    public boolean getHasBrackets() {
        return hasBrackets;
    }
    public void setHasBrackets(boolean hasBrackets) {
        this.hasBrackets = hasBrackets;
    }




    public boolean getHasDot() {
        return hasDot;
    }
    public void setHasDot(boolean hasDot) {
        this.hasDot = hasDot;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<FOLBoundVariable> getVariables() {
        return variables;
    }
}
