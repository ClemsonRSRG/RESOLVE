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
package edu.clemson.cs.r2jt.entry;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class TheoremEntry extends Entry {

    // ===========================================================
    // Variables
    // ===========================================================

    public static final int AXIOM = 1;
    public static final int THEOREM = 2;
    public static final int PROPERTY = 3;
    public static final int LEMMA = 4;
    public static final int COROLLARY = 5;

    private int kind = 0;

    private PosSymbol name = null;

    private Exp value = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TheoremEntry(PosSymbol name, int kind) {
        this.name = name;
        this.kind = kind;
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public Location getLocation() {
        return name.getLocation();
    }

    public Symbol getSymbol() {
        return name.getSymbol();
    }

    public PosSymbol getName() {
        return name;
    }

    public int getKind() {
        return kind;
    }

    public Exp getValue() {
        return value;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void setValue(Exp value) {
        this.value = value;
    }

    public String toString() {
        return "E(" + name.toString() + ")";
    }
}
