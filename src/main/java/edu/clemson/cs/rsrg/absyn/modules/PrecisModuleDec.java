/**
 * PrecisModuleDec.java
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
package edu.clemson.cs.rsrg.absyn.modules;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.absyn.ModuleDec;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.misc.UsesItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

import java.util.List;

public class PrecisModuleDec extends ModuleDec {

    public PrecisModuleDec(Location l, PosSymbol name,
            List<UsesItem> usesItems, List<Dec> decs) {
        super(l, name, usesItems, decs);
    }

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
