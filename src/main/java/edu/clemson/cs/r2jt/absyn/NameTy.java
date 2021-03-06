/*
 * NameTy.java
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

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Location;

public class NameTy extends Ty {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    // ===========================================================
    // Constructors
    // ===========================================================

    public NameTy() {};

    public NameTy(PosSymbol qualifier, PosSymbol name) {
        this.qualifier = qualifier;
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

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
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
        v.visitNameTy(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("NameTy\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public void prettyPrint() {
        if (qualifier != null)
            System.out.print(qualifier.getName() + ".");
        System.out.print(name.getName());
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        // if(qualifier != null) sb.append(qualifier.getName() + ".");
        sb.append(name.getName());
        return sb.toString();
    }

    public Ty copy() {
        PosSymbol newQualifier = null;
        if (qualifier != null)
            newQualifier = qualifier.copy();
        PosSymbol newName = name.copy();
        Ty result = new NameTy(newQualifier, newName);
        result.setMathType(getMathType());
        result.setMathTypeValue(getMathTypeValue());

        return result;
    }

    @Override
    public String toString() {
        return asString(0, 4);
    }
}
