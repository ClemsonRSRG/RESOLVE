/*
 * HypDesigExp.java
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

public class HypDesigExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The mathExp1 member. */
    private MathRefExp mathExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public HypDesigExp() {};

    public HypDesigExp(Location location, MathRefExp mathExp) {
        this.location = location;
        this.mathExp = mathExp;
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

    /** Returns the value of the mathExp1 variable. */
    public MathRefExp getMathExp() {
        return mathExp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the value of the location variable. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the value of the mathExp1 variable. */
    public void setMathExp(MathRefExp mathExp) {
        this.mathExp = mathExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new HypDesigExp(location, (MathRefExp) substitute(mathExp,
                substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitHypDesigExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("HypDesigExp\n");

        if (mathExp != null) {
            sb.append(mathExp.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add((Exp) mathExp);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        mathExp = (MathRefExp) e;
    }

    public void prettyPrint() {
        mathExp.prettyPrint();
    }

    public Exp copy() {
        MathRefExp newMathExp = (MathRefExp) (Exp.copy(mathExp));
        return new HypDesigExp(null, newMathExp);
    }

}