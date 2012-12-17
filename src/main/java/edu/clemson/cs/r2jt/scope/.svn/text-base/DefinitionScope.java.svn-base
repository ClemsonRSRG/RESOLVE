/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * DefinitionScope.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
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
    
    public Binding getBinding() { return binding; }

    public ScopeID getScopeID() { return sid; }
    
    // -----------------------------------------------------------
    // Population Methods
    // -----------------------------------------------------------

    public boolean addPermitted(Symbol sym) {
        return (!variables.containsKey(sym));
    }

    public Entry getAddObstructor(Symbol sym) {
        if (variables.containsKey(sym)) {
            return variables.get(sym);
        } else {
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
        return (types.containsKey(sym) &&
                types.get(sym).getType() instanceof ConcType);
    }

    public boolean containsLocalNonConcType(Symbol sym) {
        return (types.containsKey(sym) &&
                !(types.get(sym).getType() instanceof ConcType));
    }

    public TypeEntry getLocalType(Symbol sym) {
        return types.get(sym);
    }
}
