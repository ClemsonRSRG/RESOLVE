/*
 * GoalExp.java
 * ---------------------------------
 * Copyright (c) 2018
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
import edu.clemson.cs.r2jt.data.PosSymbol;

public class GoalExp extends LineNumberedExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The exp member. */
    private Exp exp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public GoalExp() {
        super(null);
    }

    public GoalExp(Location location, PosSymbol lineNum, Exp exp) {
        super(lineNum);
        this.location = location;
        this.exp = exp;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the exp variable. */
    public Exp getExp() {
        return exp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the exp variable to the specified value. */
    public void setExp(Exp exp) {
        this.exp = exp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new GoalExp(location, this.getLineNum(), substitute(exp,
                substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitGoalExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("GoalExp\n");

        if (myLineNumber != null) {
            printSpace(indent + increment, sb);
            sb.append("Line: " + myLineNumber.asString(0, increment));
        }

        if (exp != null) {
            sb.append(exp.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(exp);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        exp = e;
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof GoalExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        if (myLineNumber != null) {
            System.out.print(myLineNumber.getName() + ": ");
        }
        System.out.print("Goal ");
        exp.prettyPrint();
    }

    public Exp copy() {
        PosSymbol newLineNum = null;
        if (myLineNumber != null)
            newLineNum = myLineNumber.copy();
        Exp newExp = Exp.copy(exp);
        return new GoalExp(null, newLineNum, newExp);
    }

}
