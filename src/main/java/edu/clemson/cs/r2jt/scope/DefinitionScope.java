/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
