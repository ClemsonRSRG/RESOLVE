/**
 * TheoremEntry.java
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

public class TheoremEntry extends Entry {

    // ===========================================================
    // Variables
    // ===========================================================

    public static final int AXIOM = 1;
    public static final int THEOREM = 2;
    public static final int PROPERTY = 3;
    public static final int LEMMA = 4;
    public static final int COROLLARY = 5;

    private int kind = 0;

    private PosSymbol name = null;

    private Exp value = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TheoremEntry(PosSymbol name, int kind) {
        this.name = name;
        this.kind = kind;
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

    public int getKind() {
        return kind;
    }

    public Exp getValue() {
        return value;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void setValue(Exp value) {
        this.value = value;
    }

    public String toString() {
        return "E(" + name.toString() + ")";
    }
}
