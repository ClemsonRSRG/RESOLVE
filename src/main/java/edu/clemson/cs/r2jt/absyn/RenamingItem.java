/*
 * RenamingItem.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class RenamingItem extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The typeName member. */
    private Ty typeName;

    /** The name member. */
    private PosSymbol name;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RenamingItem() {};

    public RenamingItem(Ty typeName, PosSymbol name) {
        this.typeName = typeName;
        this.name = name;
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

    /** Returns the value of the typeName variable. */
    public Ty getTypeName() {
        return typeName;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the typeName variable to the specified value. */
    public void setTypeName(Ty typeName) {
        this.typeName = typeName;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitRenamingItem(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("RenamingItem\n");

        if (typeName != null) {
            sb.append(typeName.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
