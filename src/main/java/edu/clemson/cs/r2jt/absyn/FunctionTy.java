/*
 * FunctionTy.java
 * ---------------------------------
 * Copyright (c) 2017
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

public class FunctionTy extends Ty {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The domain member. */
    private Ty domain;

    /** The range member. */
    private Ty range;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FunctionTy() {};

    public FunctionTy(Ty domain, Ty range) {
        this.domain = domain;
        this.range = range;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return domain.getLocation();
    }

    /** Returns the value of the domain variable. */
    public Ty getDomain() {
        return domain;
    }

    /** Returns the value of the range variable. */
    public Ty getRange() {
        return range;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the domain variable to the specified value. */
    public void setDomain(Ty domain) {
        this.domain = domain;
    }

    /** Sets the range variable to the specified value. */
    public void setRange(Ty range) {
        this.range = range;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitFunctionTy(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FunctionTy\n");

        if (domain != null) {
            sb.append(domain.asString(indent + increment, increment));
        }

        if (range != null) {
            sb.append(range.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        sb.append(domain.toString(0));
        sb.append(" -> ");
        sb.append(range.toString(0));
        return sb.toString();
    }

    public void prettyPrint() {
        domain.prettyPrint();
        System.out.print(" -> ");
        range.prettyPrint();
    }

    public Ty copy() {
        Ty newDomain = Ty.copy(domain);
        Ty newRange = Ty.copy(range);
        return new FunctionTy(newDomain, newRange);
    }

}
