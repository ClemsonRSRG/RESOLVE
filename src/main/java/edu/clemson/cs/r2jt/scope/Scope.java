/**
 * Scope.java
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
package edu.clemson.cs.r2jt.scope;

import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;

public abstract class Scope {

    public abstract ScopeID getScopeID();

    public abstract boolean addPermitted(Symbol sym);

    public abstract Entry getAddObstructor(Symbol sym);

    public abstract void addVariable(VarEntry entry);

    public abstract boolean containsVariable(Symbol sym);

    public abstract VarEntry getVariable(Symbol sym);
}
