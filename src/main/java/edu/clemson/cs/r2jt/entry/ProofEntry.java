/**
 * ProofEntry.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.entry;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class ProofEntry extends Entry {

    // ===========================================================
    // Variables
    // ===========================================================

    private PosSymbol name = null;

    private Exp value = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProofEntry(PosSymbol name) {
        this.name = name;
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

    public Exp getValue() {
        return value;
    }

    // ============================================================
    // Public Methods
    // ============================================================

    public void setValue(Exp value) {
        this.value = value;
    }

    public String toString() {
        return "E(" + name.toString() + ")";
    }
}
