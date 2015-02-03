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

import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class ParameterVarDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The mode member. */
    private Mode mode;

    /** The name member. */
    private PosSymbol name;

    /** The ty member. */
    private Ty ty;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ParameterVarDec() {};

    public ParameterVarDec(Mode mode, PosSymbol name, Ty ty) {
        this.mode = mode;
        this.name = name;
        this.ty = ty;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the mode variable. */
    public Mode getMode() {
        return mode;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the ty variable. */
    public Ty getTy() {
        return ty;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the mode variable to the specified value. */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the ty variable to the specified value. */
    public void setTy(Ty ty) {
        this.ty = ty;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitParameterVarDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ParameterVarDec\n");

        if (mode != null) {
            printSpace(indent + increment, sb);
            sb.append(mode.toString() + "\n");
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (ty != null) {
            sb.append(ty.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public Object clone() {
        ParameterVarDec clone = new ParameterVarDec();
        clone.setName(createPosSymbol(this.getName().toString()));
        clone.setTy(Ty.copy(getTy()));
        clone.setMode((Mode) this.getMode());
        return clone;
    }

    private PosSymbol createPosSymbol(String name) {
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;

    }
}
