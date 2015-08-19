/**
 * MathAssertionDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

// Todo: Maybe change the name of this to MathTheoremDec
public class MathAssertionDec extends Dec {

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
    public ResolveConceptualElement clone() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
