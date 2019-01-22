/*
 * FuncAssignStmt.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.Location;

public class FuncAssignStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The var member. */
    private VariableExp var;

    /** The assign member. */
    private ProgramExp assign;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FuncAssignStmt() {};

    public FuncAssignStmt(Location location, VariableExp var, ProgramExp assign) {
        this.location = location;
        this.var = var;
        this.assign = assign;
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

    /** Returns the value of the var variable. */
    public VariableExp getVar() {
        return var;
    }

    /** Returns the value of the assign variable. */
    public ProgramExp getAssign() {
        return assign;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the var variable to the specified value. */
    public void setVar(VariableExp var) {
        this.var = var;
    }

    /** Sets the assign variable to the specified value. */
    public void setAssign(ProgramExp assign) {
        this.assign = assign;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitFuncAssignStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FuncAssignStmt\n");

        if (var != null) {
            sb.append(var.asString(indent + increment, increment));
        }

        if (assign != null) {
            sb.append(assign.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (var != null) {
            sb.append(var.toString(0));
            sb.append(" := ");
        }

        if (assign != null) {
            sb.append(assign.toString(0));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

}
