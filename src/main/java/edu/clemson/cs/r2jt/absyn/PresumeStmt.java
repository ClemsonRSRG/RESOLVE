/*
 * PresumeStmt.java
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

import edu.clemson.cs.r2jt.data.Location;

public class PresumeStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location myLocation;

    /** The left member. */
    private Exp myAssertion;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PresumeStmt(Location location, Exp assertion) {
        myLocation = location;
        myAssertion = assertion;
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

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitPressumeStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("PresumeStmt\n");

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
            sb.append("Presume " + myAssertion.toString(0));
        }
        else {
            sb.append("Presume true");
        }

        return sb.toString();
    }

    public PresumeStmt clone() {
        return new PresumeStmt((Location) myLocation.clone(),
                Exp.copy(myAssertion));
    }

}
