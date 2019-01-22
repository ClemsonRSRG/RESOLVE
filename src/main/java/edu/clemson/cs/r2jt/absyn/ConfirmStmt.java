/*
 * ConfirmStmt.java
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

public class ConfirmStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location myLocation;

    /** The left member. */
    private Exp myAssertion;

    /** The simplify flag */
    private boolean mySimplify;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ConfirmStmt(Location location, Exp assertion) {
        this(location, assertion, false);
    }

    public ConfirmStmt(Location location, Exp assertion, boolean simplify) {
        myLocation = location;
        myAssertion = assertion;
        mySimplify = simplify;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return myLocation;
    }

    /** Returns the value of the expression */
    public Exp getAssertion() {
        return myAssertion;
    }

    /** Returns whether we simplify the expression or not */
    public boolean getSimplify() {
        return mySimplify;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        myLocation = location;
    }

    /** Sets the right variable to the specified value. */
    public void setAssertion(Exp assertion) {
        myAssertion = assertion;
    }

    /** Sets whether we simplify the expression or not */
    public void setSimplify(boolean simplify) {
        mySimplify = simplify;
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

        if (myAssertion != null) {
            sb.append(myAssertion.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (myAssertion != null) {
            sb.append("Confirm " + myAssertion.toString(0));
        }
        else {
            sb.append("Confirm true");
        }

        return sb.toString();
    }

    public ConfirmStmt clone() {
        Location loc = null;
        if (myLocation != null) {
            loc = (Location) myLocation.clone();
        }

        return new ConfirmStmt(loc, Exp.copy(myAssertion), mySimplify);
    }
}
