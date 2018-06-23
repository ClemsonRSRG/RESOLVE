/*
 * ProofDec.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
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
