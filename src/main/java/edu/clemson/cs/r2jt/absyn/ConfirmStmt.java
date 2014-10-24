/**
 * ConfirmStmt.java
 * ---------------------------------
 * Copyright (c) 2014
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

public class ConfirmStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The left member. */
    private Exp assertion;

    /** The simplify flag */
    private boolean simplify;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ConfirmStmt() {};

    public ConfirmStmt(Location location, Exp assertion) {
        this(location, assertion, false);
    }

    public ConfirmStmt(Location location, Exp assertion, boolean simplify) {
        this.location = location;
        this.assertion = assertion;
        this.simplify = simplify;
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

    /** Returns the value of the Expression */
    public Exp getAssertion() {
        return assertion;
    }

    /** Returns whether we simplify the Expression or not */
    public boolean getSimplify() {
        return simplify;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the right variable to the specified value. */
    public void setAssertion(Exp assertion) {
        this.assertion = assertion;
    }

    /** Sets whether we simplify the Expression or not */
    public void setSimplify(boolean simplify) {
        this.simplify = simplify;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitConfirmStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ConfirmStmt\n");

        if (assertion != null) {
            sb.append(assertion.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (assertion != null) {
            sb.append("Confirm " + assertion.toString(0));
        }
        else {
            sb.append("Confirm true");
        }

        return sb.toString();
    }

    public ConfirmStmt clone() {
        return new ConfirmStmt(location, (Exp) Exp.clone(assertion), simplify);
    }
}
