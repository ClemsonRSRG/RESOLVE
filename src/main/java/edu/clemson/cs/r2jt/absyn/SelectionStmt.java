/*
 * SelectionStmt.java
 * ---------------------------------
 * Copyright (c) 2020
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

public class SelectionStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The var member. */
    private ProgramExp var;

    /** The whenpairs member. */
    private List<ChoiceItem> whenpairs;

    /** The defaultclause member. */
    private List<Statement> defaultclause;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SelectionStmt() {};

    public SelectionStmt(ProgramExp var, List<ChoiceItem> whenpairs,
            List<Statement> defaultclause) {
        this.var = var;
        this.whenpairs = whenpairs;
        this.defaultclause = defaultclause;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return var.getLocation();
    }

    /** Returns the value of the var variable. */
    public ProgramExp getVar() {
        return var;
    }

    /** Returns the value of the whenpairs variable. */
    public List<ChoiceItem> getWhenpairs() {
        return whenpairs;
    }

    /** Returns the value of the defaultclause variable. */
    public List<Statement> getDefaultclause() {
        return defaultclause;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the var variable to the specified value. */
    public void setVar(ProgramExp var) {
        this.var = var;
    }

    /** Sets the whenpairs variable to the specified value. */
    public void setWhenpairs(List<ChoiceItem> whenpairs) {
        this.whenpairs = whenpairs;
    }

    /** Sets the defaultclause variable to the specified value. */
    public void setDefaultclause(List<Statement> defaultclause) {
        this.defaultclause = defaultclause;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSelectionStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SelectionStmt\n");

        if (var != null) {
            sb.append(var.asString(indent + increment, increment));
        }

        if (whenpairs != null) {
            sb.append(whenpairs.asString(indent + increment, increment));
        }

        if (defaultclause != null) {
            sb.append(defaultclause.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
