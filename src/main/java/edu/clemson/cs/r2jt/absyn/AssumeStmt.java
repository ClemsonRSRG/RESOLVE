/**
 * AssumeStmt.java
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

public class AssumeStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The left member. */
    private Exp assertion;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AssumeStmt() {};

    public AssumeStmt(Location location, Exp assertion) {
        this.location = location;
        this.assertion = assertion;
    }

    public AssumeStmt(Exp assertion) {
        this.assertion = assertion;
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

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitAssumeStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("AssumeStmt\n");

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
            sb.append("Assume " + assertion.toString(0));
        }
        else {
            sb.append("Assume true");
        }

        return sb.toString();
    }

    public AssumeStmt clone() {
        return new AssumeStmt(location, (Exp) Exp.clone(assertion));
    }
}
