/*
 * EnhancementItem.java
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
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class EnhancementItem extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The params member. */
    private List<ModuleArgumentItem> params;

    // ===========================================================
    // Constructors
    // ===========================================================

    public EnhancementItem() {};

    public EnhancementItem(PosSymbol name, List<ModuleArgumentItem> params) {
        this.name = name;
        this.params = params;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return name.getLocation();
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the params variable. */
    public List<ModuleArgumentItem> getParams() {
        return params;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the params variable to the specified value. */
    public void setParams(List<ModuleArgumentItem> params) {
        this.params = params;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitEnhancementItem(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("EnhancementItem\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (params != null) {
            sb.append(params.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
