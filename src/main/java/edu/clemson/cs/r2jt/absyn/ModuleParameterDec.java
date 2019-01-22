/*
 * ModuleParameterDec.java
 * ---------------------------------
 * Copyright (c) 2019
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

public class ModuleParameterDec extends Dec {

    private final Dec myWrappedDec;

    public <T extends Dec & ModuleParameter> ModuleParameterDec(T dec) {
        myWrappedDec = dec;
    }

    public Dec getWrappedDec() {
        return myWrappedDec;
    }

    @Override
    public PosSymbol getName() {
        return myWrappedDec.getName();
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        myWrappedDec.accept(v);
    }

    @Override
    public String asString(int indent, int increment) {
        return myWrappedDec.asString(indent, increment);
    }
}
