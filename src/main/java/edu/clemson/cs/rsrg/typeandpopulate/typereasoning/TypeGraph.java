/**
 * TypeGraph.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTProper;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.Map;

/**
 * TODO:
 */
public class TypeGraph {

    public final MTProper CLS = new MTProper(this, null, true, "MType");
    public final MTProper BOOLEAN = new MTProper(this, CLS, false, "B");
    public final MTProper EMPTY_SET =
            new MTProper(this, CLS, false, "Empty_Set");
    public final MTProper VOID = new MTProper(this, CLS, false, "Void");

    public boolean isSubtype(MTType subtype, MTType supertype) {
        return false;
    }

    public boolean isKnownToBeIn(Exp value, MTType expected) {
        return false;
    }

    public boolean isKnownToBeIn(MTType value, MTType expected) {
        return false;
    }

    public static MTType getCopyWithVariablesSubstituted(MTType original,
            Map<String, MTType> substitutions) {
        return null;
    }

    public static <T extends Exp> T getCopyWithVariablesSubstituted(T original,
            Map<String, MTType> substitutions) {
        return null;
    }

}