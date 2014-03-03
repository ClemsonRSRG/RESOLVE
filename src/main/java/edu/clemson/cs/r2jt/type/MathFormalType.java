/**
 * MathFormalType.java
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
 * MathFormalType.java
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
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class MathFormalType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleID id;

    private PosSymbol name;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MathFormalType(ModuleID id, PosSymbol name) {
        this.id = id;
        this.name = name;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================    

    public ModuleID getModuleID() {
        return id;
    }

    public Symbol getSymbol() {
        return name.getSymbol();
    }

    public PosSymbol getName() {
        return name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type instantiate(ScopeID sid, Binding binding) {
        return this;
    }

    public TypeName getProgramName() {
        return new TypeName(id, null, name);
    }

    public String getRelativeName(Location loc) {
        return null;
    }

    public Type toMath() {
        return this;
    }

    public String toString() {
        return (id.toString() + "." + name.toString());
    }

    public String asString() {
        return (id.toString() + "." + name.toString());
    }
}
