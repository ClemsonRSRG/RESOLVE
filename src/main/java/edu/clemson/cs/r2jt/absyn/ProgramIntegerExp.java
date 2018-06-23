/*
 * ProgramIntegerExp.java
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

public class ProgramIntegerExp extends ProgramExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The value member. */
    private int value;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProgramIntegerExp() {};

    public ProgramIntegerExp(Location location, int value) {
        this.location = location;
        this.value = value;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new ProgramIntegerExp(location, value);
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

    /** Returns the value of the value variable. */
    public int getValue() {
        return value;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the value variable to the specified value. */
    public void setValue(int value) {
        this.value = value;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProgramIntegerExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProgramIntegerExp\n");

        printSpace(indent + increment, sb);
        sb.append(value + "\n");

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append(value);

        return sb.toString();
    }

    public Exp replace(Exp old, Exp replacement) {

        if (old instanceof ProgramIntegerExp) {
            if (((ProgramIntegerExp) old).getValue() == value) {
                return (Exp) Exp.clone(replacement);
            }
        }

        return null;
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        return new List<Exp>();
    }

    public void setSubExpression(int index, Exp e) {

    }

    public ProgramIntegerExp copy() {
        ProgramIntegerExp result = new ProgramIntegerExp(location, value);
        return result;
    }
}
