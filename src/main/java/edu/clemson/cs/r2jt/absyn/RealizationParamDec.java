/**
 * RealizationParamDec.java
 * ---------------------------------
 * Copyright (c) 2015
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

public class RealizationParamDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The conceptName member. */
    private PosSymbol conceptName;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RealizationParamDec() {};

    public RealizationParamDec(PosSymbol name, PosSymbol conceptName) {
        this.name = name;
        this.conceptName = conceptName;
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

    /** Returns the value of the conceptName variable. */
    public PosSymbol getConceptName() {
        return conceptName;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the conceptName variable to the specified value. */
    public void setConceptName(PosSymbol conceptName) {
        this.conceptName = conceptName;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitRealizationParamDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("RealizationParamDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (conceptName != null) {
            sb.append(conceptName.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
