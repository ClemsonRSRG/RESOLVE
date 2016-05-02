/**
 * SetCollectionExp.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

public class SetCollectionExp extends Exp {

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
     * <p>Returns true if the variable is found in any sub
     * expression of this one.</p>
     *
     * @param varName Name of the variable
     * @param IsOldExp True/False depending if this is an
     *                 expression with a "#" symbol in front
     *                 indicating that it is an old expression.
     *
     * @return True if it contains this variable,
     *         false otherwise.
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

    /**
     * <p>Creates a deep copy of the set collection.</p>
     *
     * @return A copy of the original set collection.
     */
    public Exp copy() {
        List<VarExp> newVars = new List<VarExp>();

        for (VarExp v : myVars) {
            newVars.add((VarExp) Exp.copy(v));
        }
        return new SetCollectionExp((Location) myLocation.clone(), newVars);
    }

    /**
     * <p>Test if two expressions are the same.</p>
     *
     * @param exp Expression to check
     *
     * @return True if equal, false otherwise.
     */
    public boolean equals(Exp exp) {
        boolean retVal = true;
        if (exp instanceof SetCollectionExp) {
            List<VarExp> expVars = ((SetCollectionExp) exp).getVars();
            if (expVars.size() != myVars.size()) {
                retVal = false;
            }

            for (int i = 0; i < expVars.size() && retVal; i++) {
                if (!expVars.get(i).equals(myVars.get(i))) {
                    retVal = false;
                }
            }
        }
        else {
            retVal = false;
        }

        return retVal;
    }

    /**
     * <p>Converts all variable expressions inside
     * the set collection to expressions and returns them
     * in a new list.</p>
     *
     * @return List containing all expressions.
     */
    public List<Exp> getSubExpressions() {
        List<Exp> expList = new List<Exp>();
        for (VarExp v : myVars) {
            expList.add(v);
        }
        return expList;
    }

    /**
     * <p>This prints the expression to the console
     * in a human readable format.</p>
     */
    public void prettyPrint() {
        System.out.print("{ ");
        boolean first = true;
        for (VarExp v : myVars) {
            if (first) {
                first = false;
            }
            else {
                System.out.print(", ");
            }
            v.prettyPrint();
        }
        System.out.print(" }");
    }

    /**
     * <p>Replaces the expression at the indicated
     * index.</p>
     *
     * @param index Position in our list.
     * @param e New expression to be replaced.
     */
    public void setSubExpression(int index, Exp e) {
        if (e instanceof VarExp) {
            myVars.set(index, (VarExp) e);
        }
    }

    /**
     * <p>Returns a debugging string of this class.</p>
     *
     * @return A string used for debugging purposes
     */
    public String toString() {
        return myVars.toString();
    }
}