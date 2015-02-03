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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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

import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
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

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getFunctionTyType(this);
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
