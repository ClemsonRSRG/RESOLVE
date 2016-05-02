/**
 * MathAssertionDec.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.declarations;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the mathematical assertion declarations
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class MathAssertionDec extends Dec {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs either a mathematical axiom, theorem,
     * corollary, lemma or property assertion.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name Name of the assertion declaration.
     */
    public MathAssertionDec(Location l, PosSymbol name) {
        super(l, name);
    }

    //In the future:
    //public MathAssertionDec(Location l, PosSymbol name, Exp assertion) {
    //    super(l, name);
    //    this.assertion = assertion;
    //}

    @Override
    public String asString(int indentSize, int innerIndentSize) {
        return null;
    }

    @Override
    public MathAssertionDec clone() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
