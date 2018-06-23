/*
 * ProgramStringExp.java
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

public class ProgramStringExp extends ProgramExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The value member. */
    private String value;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProgramStringExp() {};

    public ProgramStringExp(Location location, String value) {
        this.location = location;
        this.value = value;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new ProgramStringExp(location, value);
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
    public String getValue() {
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
    public void setValue(String value) {
        this.value = value;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProgramStringExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProgramStringExp\n");

        printSpace(indent + increment, sb);
        sb.append(value + "\n");

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);
        if (value != null) {
            sb.append(value.toString());
        }
        return sb.toString();
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

    @Override
    protected ProgramStringExp copy() {
        ProgramStringExp c = new ProgramStringExp(location, value);
        c.setMathType(this.myMathType);
        c.setMathTypeValue(this.myMathTypeValue);
        return c;
    }

}
