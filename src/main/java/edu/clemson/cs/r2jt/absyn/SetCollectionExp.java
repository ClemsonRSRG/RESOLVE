package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

public class SetCollectionExp extends SetExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /**
     * <p>The file location.</p>
     */
    private Location myLocation;

    /**
     * <p>The list of variable expressions
     * in this set collection.</p>
     */
    private List<VarExp> myVars;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SetCollectionExp(Location location, List<VarExp> vars) {
        myLocation = location;
        myVars = vars;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new SetCollectionExp(myLocation, myVars);
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /**
     * <p>Returns the value of the location variable.</p>
     *
     * @return <code>Location</code>
     */
    public Location getLocation() {
        return myLocation;
    }

    /**
     * <p>Returns the list of the variables.</p>
     *
     * @return <code>VarExp</code> list.
     */
    public List<VarExp> getVars() {
        return myVars;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /**
     * <p>Sets the location variable to the specified value.</p>
     *
     * @param location New location for this expression.
     */
    public void setLocation(Location location) {
        myLocation = location;
    }

    /**
     * <p>Sets the list of variables to the specified value.</p>
     *
     * @param vars New list of variable expressions.
     */
    public void setVars(List<VarExp> vars) {
        myVars = vars;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSetCollectionExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getSetCollectionExpType(this);
    }

    /**
     * <p>Returns a formatted text string of this class.</p>
     *
     * @param indent Amount of spaces to indent.
     * @param increment Amount of spaces to increment.
     *
     * @return A formatted string.
     */
    public String asString(int indent, int increment) {
        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SetCollectionExp\n");

        if (myVars != null) {
            if (myVars.isEmpty()) {
                sb.append("");
            }
            else {
                sb.append(myVars.asString(indent, increment));
            }
        }

        return sb.toString();
    }

    /**
     * <p>Returns a debugging string of this class.</p>
     *
     * @return A string used for debugging purposes
     */
    public String toString() {
        return myVars.toString();
    }

    /**
     * <p>Returns true if the variable is found in any sub
     * expression of this one.</p>
     *
     * @param
     * @param
     *
     * @return
     */
    public boolean containsVar(String varName, boolean IsOldExp) {
        for (VarExp v : myVars) {
            if (v != null) {
                if (v.containsVar(varName, IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(where);
        list.add(body);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
            case 0:
                where = e;
                break;
            case 1:
                body = e;
                break;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof SetExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        System.out.print("{ ");
        var.prettyPrint();
        System.out.print(", ");
        if (where != null) {
            where.prettyPrint();
            System.out.print(", ");
        }
        body.prettyPrint();
        System.out.print(" }");
    }

    public Exp copy() {
        MathVarDec newVar = var.copy();
        List<VarExp> newVars = new List<VarExp>();

        for (VarExp v : vars) {
            newVars.add((VarExp) Exp.copy(v));
        }

        Exp newWhere = null;
        if (where != null)
            newWhere = Exp.copy(where);
        Exp newBody = Exp.copy(body);
        return new SetExp(null, newVar, newWhere, newBody, newVars);
    }

}