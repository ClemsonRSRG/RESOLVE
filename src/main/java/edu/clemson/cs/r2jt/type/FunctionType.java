/**
 * FunctionType.java
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
/*
 * FunctionType.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class FunctionType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private Type domain;

    private Type range;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FunctionType(Type domain, Type range) {
        this.domain = domain;
        this.range = range;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type getDomain() {
        return domain;
    }

    public Type getRange() {
        return range;
    }

    public Type instantiate(ScopeID sid, Binding binding) {
        return new FunctionType(domain.instantiate(sid, binding), range
                .instantiate(sid, binding));
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
        return domain.toString() + " -> " + range.toString();
    }

    public String asString() {
        return domain.asString() + " -> " + range.asString();
    }
}
