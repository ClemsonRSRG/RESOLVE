/**
 * ProcedureScope.java
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

    private Map<Symbol, ModuleEntry> facilities =
            new Map<Symbol, ModuleEntry>();
    private Map<Symbol, VarEntry> variables = new Map<Symbol, VarEntry>();

    private Binding binding = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProcedureScope(ModuleScope scope, ScopeID sid,
            CompileEnvironment instanceEnvironment) {
        this.sid = sid;
        this.moduleScope = scope;
        binding = new Binding(this, instanceEnvironment);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public ScopeID getScopeID() {
        return sid;
    }

    public ModuleScope getModuleScope() {
        return moduleScope;
    }

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
        if (facilities.containsKey(sym) || variables.containsKey(sym)) {
            return false;
        }
        else {
            return true;
        }
    }

    public Entry getAddObstructor(Symbol sym) {
        if (facilities.containsKey(sym)) {
            return facilities.get(sym);
        }
        else if (variables.containsKey(sym)) {
            return variables.get(sym);
        }
        else {
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

    public Binding getBinding() {
        return binding;
    }

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
