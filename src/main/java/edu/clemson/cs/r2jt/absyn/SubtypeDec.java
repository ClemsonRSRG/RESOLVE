/*
 * SubtypeDec.java
 * ---------------------------------
 * Copyright (c) 2021
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
