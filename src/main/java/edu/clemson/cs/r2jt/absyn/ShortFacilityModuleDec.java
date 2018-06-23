/*
 * ShortFacilityModuleDec.java
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

public class ShortFacilityModuleDec extends ModuleDec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The dec member. */
    private FacilityDec dec;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ShortFacilityModuleDec() {};

    public ShortFacilityModuleDec(PosSymbol name, FacilityDec dec,
            List<UsesItem> usesItems) {
        this.name = name;
        this.dec = dec;
        this.usesItems = usesItems;
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

    /** Returns the value of the dec variable. */
    public FacilityDec getDec() {
        return dec;
    }

    /** Returns the value of the usesItems variable. */
    public List<UsesItem> getUsesItems() {
        return usesItems;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the dec variable to the specified value. */
    public void setDec(FacilityDec dec) {
        this.dec = dec;
    }

    /** Sets the usesItems variable to the specified value. */
    public void setUsesItems(List<UsesItem> usesItems) {
        this.usesItems = usesItems;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitShortFacilityModuleDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ShortFacilityModuleDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (dec != null) {
            sb.append(dec.asString(indent + increment, increment));
        }

        if (usesItems != null) {
            sb.append(usesItems.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
