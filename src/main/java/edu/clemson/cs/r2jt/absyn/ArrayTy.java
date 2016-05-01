/**
 * ArrayTy.java
 * ---------------------------------
 * Copyright (c) 2016
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

public class ArrayTy extends Ty {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The lo member. */
    private ProgramExp lo;

    /** The hi member. */
    private ProgramExp hi;

    /** The entryType member. */
    private Ty entryType;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ArrayTy() {
    // Empty
    }

    public ArrayTy(Location location, ProgramExp lo, ProgramExp hi, Ty entryType) {
        this.location = location;
        this.lo = lo;
        this.hi = hi;
        this.entryType = entryType;
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

    /** Returns the value of the lo variable. */
    public ProgramExp getLo() {
        return lo;
    }

    /** Returns the value of the hi variable. */
    public ProgramExp getHi() {
        return hi;
    }

    /** Returns the value of the entryType variable. */
    public Ty getEntryType() {
        return entryType;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the lo variable to the specified value. */
    public void setLo(ProgramExp lo) {
        this.lo = lo;
    }

    /** Sets the hi variable to the specified value. */
    public void setHi(ProgramExp hi) {
        this.hi = hi;
    }

    /** Sets the entryType variable to the specified value. */
    public void setEntryType(Ty entryType) {
        this.entryType = entryType;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitArrayTy(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ArrayTy\n");

        if (lo != null) {
            sb.append(lo.asString(indent + increment, increment));
        }

        if (hi != null) {
            sb.append(hi.asString(indent + increment, increment));
        }

        if (entryType != null) {
            sb.append(entryType.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append("Array ");
        sb.append(lo.toString(0));
        sb.append("..");
        sb.append(hi.toString(0));
        sb.append(" of ");
        sb.append(entryType.toString(0));
        return sb.toString();
    }
}
