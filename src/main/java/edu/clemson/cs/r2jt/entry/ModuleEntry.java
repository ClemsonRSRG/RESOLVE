/**
 * ModuleEntry.java
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
package edu.clemson.cs.r2jt.entry;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.scope.ModuleScope;

public class ModuleEntry extends Entry {

    // ===========================================================
    // Variables
    // ===========================================================

    //private Environment env = Environment.getInstance();

    private PosSymbol name = null;

    private ModuleID id = null;

    private ModuleScope scope = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ModuleEntry(PosSymbol name, ModuleScope scope) {
        this.name = name;
        this.id = null;
        this.scope = scope;
    }

    public ModuleEntry(ModuleID id, CompileEnvironment env) {
        this.name = env.getModuleDec(id).getName();
        this.id = id;
        this.scope = env.getSymbolTable(id).getModuleScope();
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public Location getLocation() {
        return name.getLocation();
    }

    public Symbol getSymbol() {
        return name.getSymbol();
    }

    public PosSymbol getName() {
        return name;

    }

    public boolean isFacilityDec() {
        return (id == null);
    }

    public ModuleScope getModuleScope() {
        return scope;
    }

    public String toString() {
        return "E(" + scope.getModuleID().toString() + ")";
    }

}
