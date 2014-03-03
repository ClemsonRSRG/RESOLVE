/**
 * VoidType.java
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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class VoidType extends Type {

    // ===========================================================
    // Constructors
    // ===========================================================

    public VoidType() {
        ;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type instantiate(ScopeID sid, Binding binding) {
        return this;
    }

    public TypeName getProgramName() {
        return null;
    }

    public String getRelativeName(Location loc) {
        return null;
    }

    public Type toMath() {
        return this;
    }

    public String toString() {
        return new String("Void");
    }

    public String asString() {
        return new String("Void");
    }
}
