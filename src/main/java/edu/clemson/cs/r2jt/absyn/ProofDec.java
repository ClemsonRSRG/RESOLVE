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

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class ProofDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    public static final int THEOREM = 1;
    public static final int PROPERTY = 2;
    public static final int LEMMA = 3;
    public static final int COROLLARY = 4;

    /** The kind member. **/
    private int kind;

    /** The name member. **/
    private PosSymbol name;

    /** The statements member. **/
    private List<Exp> statements;

    /** The baseCase member. **/
    private List<Exp> baseCase;

    /** The inductiveCase member. **/
    private List<Exp> inductiveCase;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProofDec() {};

    public ProofDec(int kind, PosSymbol name, List<Exp> statements,
            List<Exp> baseCase, List<Exp> inductiveCase) {
        this.kind = kind;
        this.name = name;
        this.statements = statements;
        this.baseCase = baseCase;
        this.inductiveCase = inductiveCase;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the kind variable. */
    public int getKind() {
        return kind;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the statements variable. */
    public List<Exp> getStatements() {
        return statements;
    }

    /** Returns the value of the baseCase variable. */
    public List<Exp> getBaseCase() {
        return baseCase;
    }

    /** Returns the value of the inductiveCase variable. */
    public List<Exp> getInductiveCase() {
        return inductiveCase;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the kind variable to the specified value. */
    public void setKind(int kind) {
        this.kind = kind;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Set the statements variable to the specified value. */
    public void setStatements(List<Exp> statements) {
        this.statements = statements;
    }

    /** Set the baseCase variable to the specified value. */
    public void setBaseCase(List<Exp> baseCase) {
        this.baseCase = baseCase;
    }

    /** Set the inductiveCase variable to the specified value. */
    public void setInductiveCase(List<Exp> inductiveCase) {
        this.inductiveCase = inductiveCase;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProofDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProofDec\n");

        switch (kind) {
        case THEOREM:
            printSpace(indent + increment, sb);
            sb.append("THEOREM\n");
            break;
        case LEMMA:
            printSpace(indent + increment, sb);
            sb.append("LEMMA\n");
            break;
        case PROPERTY:
            printSpace(indent + increment, sb);
            sb.append("PROPERTY\n");
            break;
        case COROLLARY:
            printSpace(indent + increment, sb);
            sb.append("COROLLARY\n");
            break;
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (statements != null) {
            sb.append(statements.asString(indent + increment, increment));
        }

        if (baseCase != null) {
            sb.append(baseCase.asString(indent + increment, increment));
        }

        if (inductiveCase != null) {
            sb.append(inductiveCase.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

}
