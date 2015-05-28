/**
 * JustifiedExp.java
 * ---------------------------------
 * Copyright (c) 2015
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

public class JustifiedExp extends LineNumberedExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The exp member. */
    private Exp exp;

    /** The justification member. */
    private JustificationExp justification;

    // ===========================================================
    // Constructors
    // ===========================================================

    public JustifiedExp() {
        super(null);
    }

    public JustifiedExp(Location location, PosSymbol lineNum, Exp exp,
            JustificationExp justification) {
        super(lineNum);
        this.location = location;
        this.exp = exp;
        this.justification = justification;
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

    /** Returns the value of the justification variable. */
    public JustificationExp getJustification() {
        return justification;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the value of the location variable. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the value of the exp variable. */
    public void setExp(Exp exp) {
        this.exp = exp;
    }

    /** Sets the value of the justification variable. */
    public void setJustification(JustificationExp justification) {
        this.justification = justification;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new JustifiedExp(location, this.getLineNum(), substitute(exp,
                substitutions), justification);
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitJustifiedExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("JustifiedExp\n");

        if (myLineNumber != null) {
            printSpace(indent + increment, sb);
            sb.append("Line: " + myLineNumber.asString(0, increment));
        }

        if (exp != null) {
            sb.append(exp.asString(indent + increment, increment));
        }

        if (justification != null) {
            sb.append(justification.asString(indent + increment, increment));
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
        if (!(e2 instanceof JustifiedExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        if (myLineNumber != null)
            System.out.print(myLineNumber.getName() + ": ");
        exp.prettyPrint();
        System.out.print("   ");
        justification.prettyPrint();
    }

    public Exp copy() {
        PosSymbol newLineNum = null;
        if (myLineNumber != null)
            newLineNum = myLineNumber.copy();
        Exp newExp = Exp.copy(exp);
        JustificationExp newJustification =
                (JustificationExp) (Exp.copy(justification));
        return new JustifiedExp(null, newLineNum, newExp, newJustification);
    }

}