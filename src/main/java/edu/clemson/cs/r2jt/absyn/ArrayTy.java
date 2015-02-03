/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

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

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getArrayTyType(this);
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
