/**
 * VarEntry.java
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
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.Scope;
import edu.clemson.cs.r2jt.scope.ScopeID;
import edu.clemson.cs.r2jt.type.Type;

public class VarEntry extends Entry {

    // ===========================================================
    // Variables
    // ===========================================================

    private Scope scope = null;

    private Mode mode = null;

    private PosSymbol name = null;

    private Type type = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public VarEntry(Scope scope, Mode mode, PosSymbol name, Type type) {
        this.scope = scope;
        this.mode = mode;
        this.name = name;
        this.type = type;
    }

    /*
     public VarEntry(Mode mode, PosSymbol name, Type type) {
     this.mode = mode;
     this.name = name;
     this.type = type;
     }
     */

    // ===========================================================
    // Accessors
    // ===========================================================

    public Scope getScope() {
        return scope;
    }

    public Location getLocation() {
        return name.getLocation();
    }

    public Symbol getSymbol() {
        return name.getSymbol();
    }

    public PosSymbol getName() {
        return name;
    }

    public Mode getMode() {
        return mode;
    }

    public Type getType() {
        return type;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public VarEntry instantiate(ScopeID sid, Binding binding) {
        return new VarEntry(binding.getScope(), mode, name, type.instantiate(
                sid, binding));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("E(");
        sb.append(mode.toString() + " ");
        sb.append(name.toString());
        sb.append(": ");
        sb.append(type.toString());
        sb.append(")");
        return sb.toString();
    }
}
