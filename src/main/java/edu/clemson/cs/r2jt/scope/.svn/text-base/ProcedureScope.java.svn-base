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
 * ProcedureScope.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.scope;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

public class ProcedureScope extends Scope {

    // ===========================================================
    // Variables
    // ===========================================================

    private ScopeID sid = null;

    private ModuleScope moduleScope = null;

    private List<Entry> parameters = new List<Entry>();

    private Map<Symbol, ModuleEntry> facilities
        = new Map<Symbol, ModuleEntry>();
    private Map<Symbol, VarEntry> variables
        = new Map<Symbol, VarEntry>();

    private Binding binding = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProcedureScope(ModuleScope scope, ScopeID sid, CompileEnvironment instanceEnvironment) {
        this.sid = sid;
        this.moduleScope = scope;
        binding = new Binding(this, instanceEnvironment);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public ScopeID getScopeID() { return sid; }

    public ModuleScope getModuleScope() { return moduleScope; }

    // -----------------------------------------------------------
    // Import Methods
    // -----------------------------------------------------------

    public Iterator<ModuleScope> getVisibleModules() {
        List<ModuleScope> modules = new List<ModuleScope>();
        Iterator<Symbol> i = facilities.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            modules.add(facilities.get(sym).getModuleScope());
        }
        return modules.iterator();
    }

    public boolean containsVisibleModule(Symbol qual) {
        return (facilities.containsKey(qual));
    }

    public ModuleScope getVisibleModule(Symbol qual) {
        return (facilities.get(qual).getModuleScope());
    }

    // -----------------------------------------------------------
    // Population Methods
    // -----------------------------------------------------------

    public boolean addPermitted(Symbol sym) {
        if (  facilities.containsKey(sym) ||
              variables.containsKey(sym)) {
            return false;
        } else {
            return true;
        }
    }

    public Entry getAddObstructor(Symbol sym) {
        if (facilities.containsKey(sym)) {
            return facilities.get(sym);
        } else if (variables.containsKey(sym)) {
            return variables.get(sym);
        } else {
            assert false : "getAddObstructor failed";
            return null;
        }
    }

    public void addFacility(ModuleEntry entry) {
        facilities.put(entry.getSymbol(), entry);
    }

    public void addVariable(VarEntry entry) {
        variables.put(entry.getSymbol(), entry);
    }
    
  

    // -----------------------------------------------------------
    // Get Entry Methods
    // -----------------------------------------------------------

    public boolean containsVariable(Symbol sym) {
        return variables.containsKey(sym);
    }

    public VarEntry getVariable(Symbol sym) {
        return variables.get(sym);
    }

    public List<ModuleScope> getFacilities() {
        List<ModuleScope> scopes = new List<ModuleScope>();
        Iterator<Symbol> i = facilities.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            ModuleScope scope = facilities.get(sym).getModuleScope();
            scopes.add(scope);
        }
        return scopes;
    }

    // -----------------------------------------------------------
    // Get Binding Method
    // -----------------------------------------------------------

    public Binding getBinding() { return binding; }

    // -----------------------------------------------------------
    // To String Method
    // -----------------------------------------------------------

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("==================================================\n");
        sb.append("Procedure Scope for " + sid.toString());
        sb.append("\n");
        sb.append("==================================================\n\n");
        sb.append("Parameters: " + parameters.toString() + "\n");
        sb.append("---------- Facilities ----------------------------\n");
        sb.append(facilities.toString() + "\n");
        sb.append("---------- Variables -----------------------------\n");
        sb.append(variables.toString() + "\n");
        sb.append(binding.toString());
        sb.append("----- end procedure scope ---------------------------\n");
        return sb.toString();
    }
        

}
