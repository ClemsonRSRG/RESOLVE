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

import edu.clemson.cs.r2jt.data.PosSymbol;

public class SubtypeDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The qualifier1 member. */
    private PosSymbol qualifier1;

    /** The name1 member. */
    private PosSymbol name1;

    /** The qualifier2 member. */
    private PosSymbol qualifier2;

    /** The name2 member. */
    private PosSymbol name2;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SubtypeDec(PosSymbol qualifier1, PosSymbol name1,
            PosSymbol qualifier2, PosSymbol name2) {
        this.qualifier1 = qualifier1;
        this.name1 = name1;
        this.qualifier2 = qualifier2;
        this.name2 = name2;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the name1 variable. */
    public PosSymbol getName() {
        return name1;
    }

    /** Returns the value of the qualifier1 variable. */
    public PosSymbol getQualifier1() {
        return qualifier1;
    }

    /** Returns the value of the name1 variable. */
    public PosSymbol getName1() {
        return name1;
    }

    /** Returns the value of the qualifier2 variable. */
    public PosSymbol getQualifier2() {
        return qualifier2;
    }

    /** Returns the value of the name2 variable. */
    public PosSymbol getName2() {
        return name2;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name1 variable to the specified value. */
    public void setName1(PosSymbol name1) {
        this.name1 = name1;
    }

    /** Sets the name2 variable to the specified value. */
    public void setName2(PosSymbol name2) {
        this.name2 = name2;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSubtypeDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SubtypeDec\n");

        if (qualifier1 != null) {
            sb.append(qualifier1.asString(indent + increment, increment));
        }

        if (name1 != null) {
            sb.append(name1.asString(indent + increment, increment));
        }

        if (qualifier2 != null) {
            sb.append(qualifier2.asString(indent + increment, increment));
        }

        if (name2 != null) {
            sb.append(name2.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
