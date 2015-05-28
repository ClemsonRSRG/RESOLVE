/**
 * SwapStmt.java
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

import edu.clemson.cs.r2jt.data.Location;

public class SwapStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The left member. */
    private VariableExp left;

    /** The right member. */
    private VariableExp right;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SwapStmt() {};

    public SwapStmt(Location location, VariableExp left, VariableExp right) {
        this.location = location;
        this.left = left;
        this.right = right;
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

    /** Returns the value of the left variable. */
    public VariableExp getLeft() {
        return left;
    }

    /** Returns the value of the right variable. */
    public VariableExp getRight() {
        return right;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the left variable to the specified value. */
    public void setLeft(VariableExp left) {
        this.left = left;
    }

    /** Sets the right variable to the specified value. */
    public void setRight(VariableExp right) {
        this.right = right;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSwapStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SwapStmt\n");

        if (left != null) {
            sb.append(left.asString(indent + increment, increment));
        }

        if (right != null) {
            sb.append(right.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (left != null) {
            sb.append(left.toString(0) + " :=: ");
        }

        if (right != null) {
            sb.append(right.toString(0));
        }

        return sb.toString();
    }
}
