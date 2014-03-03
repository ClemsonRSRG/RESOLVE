/**
 * TypeScope.java
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

import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;

public class TypeScope extends Scope {

    // ===========================================================
    // Variables
    // ===========================================================

    private ScopeID sid = null;

    private Map<Symbol, VarEntry> variables = new Map<Symbol, VarEntry>();

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeScope(ScopeID sid) {
        this.sid = sid;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public ScopeID getScopeID() {
        return sid;
    }

    // -----------------------------------------------------------
    // Population Methods
    // -----------------------------------------------------------

    public boolean addPermitted(Symbol sym) {
        return (!variables.containsKey(sym));
    }

    public Entry getAddObstructor(Symbol sym) {
        return variables.get(sym);
    }

    public void addVariable(VarEntry entry) {
        variables.put(entry.getSymbol(), entry);
    }

    public boolean containsVariable(Symbol sym) {
        return variables.containsKey(sym);
    }

    public VarEntry getVariable(Symbol sym) {
        return variables.get(sym);
    }
}
