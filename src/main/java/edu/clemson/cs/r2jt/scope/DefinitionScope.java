/**
 * DefinitionScope.java
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
import edu.clemson.cs.r2jt.entry.Entry;
import edu.clemson.cs.r2jt.entry.ModuleEntry;
import edu.clemson.cs.r2jt.entry.TypeEntry;
import edu.clemson.cs.r2jt.entry.VarEntry;
import edu.clemson.cs.r2jt.type.ConcType;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

public class DefinitionScope extends Scope {

    // ===========================================================
    // Variables
    // ===========================================================

    private ScopeID sid = null;
    private ErrorHandler err;

    private Map<Symbol, VarEntry> variables = new Map<Symbol, VarEntry>();

    private Binding binding;

    private Map<Symbol, TypeEntry> types = new Map<Symbol, TypeEntry>();

    // ===========================================================
    // Constructors
    // ===========================================================

    public DefinitionScope(ScopeID sid, CompileEnvironment instanceEnvironment) {
        this.sid = sid;
        this.err = instanceEnvironment.getErrorHandler();
        binding = new Binding(this, instanceEnvironment);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Binding getBinding() {
        return binding;
    }

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
        if (variables.containsKey(sym)) {
            return variables.get(sym);
        }
        else {
            assert false : "getAddObstructor failed";
            return null;
        }
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

    public Map<Symbol, TypeEntry> getTypes() {
        return types;
    }

    public void addType(TypeEntry entry) {
        types.put(entry.getSymbol(), entry);
    }

    public boolean containsLocalType(Symbol sym) {
        return (types.containsKey(sym));
    }

    public boolean containsLocalConcType(Symbol sym) {
        return (types.containsKey(sym) && types.get(sym).getType() instanceof ConcType);
    }

    public boolean containsLocalNonConcType(Symbol sym) {
        return (types.containsKey(sym) && !(types.get(sym).getType() instanceof ConcType));
    }

    public TypeEntry getLocalType(Symbol sym) {
        return types.get(sym);
    }
}
