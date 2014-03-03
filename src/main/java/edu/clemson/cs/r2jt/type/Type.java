/**
 * Type.java
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

import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;
import edu.clemson.cs.r2jt.data.Location;

public abstract class Type {

    // ==========================================================
    // Public Methods
    // ==========================================================

    public abstract Type instantiate(ScopeID sid, Binding binding);

    public abstract TypeName getProgramName();

    public abstract String getRelativeName(Location loc);

    public abstract Type toMath();

    public abstract String asString();

}
