/*
 * MathAssertionDec.java
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

import edu.clemson.cs.r2jt.data.PosSymbol;

public class MathAssertionDec extends Dec {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int AXIOM = 1;
    public static final int THEOREM = 2;
    public static final int PROPERTY = 3;
    public static final int LEMMA = 4;
    public static final int COROLLARY = 5;

    public static enum TheoremSubtype {
        NONE, ASSOCIATIVITY, COMMUTATIVITY
    };

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The kind member. */
    private int kind;

    /** The assertion member. */
    private Exp assertion;

    /**
     * <p>For <em>theorem</em>s only, defines any special properties of the
     * theorem, such as if it is flagged as an associativity or commutativity
     * theorem in the definition.</p>
     */
    private TheoremSubtype mySubtype;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MathAssertionDec() {};

    public MathAssertionDec(PosSymbol name, int kind, Exp assertion) {
        this.name = name;
        this.kind = kind;
        this.assertion = assertion;
        mySubtype = TheoremSubtype.NONE;
    }

    public MathAssertionDec(PosSymbol name, Exp assertion,
            TheoremSubtype theoremSubtype) {
        this.name = name;
        this.kind = THEOREM;
        this.assertion = assertion;
        mySubtype = theoremSubtype;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the kind variable. */
    public int getKind() {
        return kind;
    }

    /** Returns the value of the assertion variable. */
    public Exp getAssertion() {
        return assertion;
    }

    /**
     * <p>Returns the specific subtype of the theorem reprsented by this
     * assertion.</p>
     */
    public TheoremSubtype getTheoremSubtype() {
        return mySubtype;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the kind variable to the specified value. */
    public void setKind(int kind) {
        this.kind = kind;
    }

    /** Sets the assertion variable to the specified value. */
    public void setAssertion(Exp assertion) {
        this.assertion = assertion;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitMathAssertionDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("MathAssertionDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        printSpace(indent + increment, sb);
        sb.append(printConstant(kind) + "\n");

        if (assertion != null) {
            sb.append(assertion.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("AXIOM");
            break;
        case 2:
            sb.append("THEOREM");
            break;
        case 3:
            sb.append("PROPERTY");
            break;
        case 4:
            sb.append("LEMMA");
            break;
        case 5:
            sb.append("COROLLARY");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return assertion.asString(0, 4);
    }
}
