/**
 * OperationEntry.java
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

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.Scope;
import edu.clemson.cs.r2jt.scope.ScopeID;
import edu.clemson.cs.r2jt.type.Type;

public class OperationEntry extends Entry {

    // ===========================================================
    // Variables
    // ===========================================================

    private Scope scope = null;

    private PosSymbol name = null;

    private List<VarEntry> params = new List<VarEntry>();

    private Type type = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OperationEntry(Scope scope, PosSymbol name, List<VarEntry> params,
            Type type) {
        this.scope = scope;
        this.name = name;
        this.params.addAll(params);
        this.type = type;
    }

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

    public Iterator<VarEntry> getParameters() {
        return params.iterator();
    }

    public Type getType() {
        return type;
    }

    public int getParamNum() {
        return params.size();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public OperationEntry instantiate(ScopeID sid, Binding binding) {
        List<VarEntry> params2 = new List<VarEntry>();
        Iterator<VarEntry> i = params.iterator();
        while (i.hasNext()) {
            VarEntry entry = i.next();
            params2.add(entry.instantiate(sid, binding));
        }
        return new OperationEntry(binding.getScope(), name, params2, type
                .instantiate(sid, binding));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("E(");
        sb.append(name.toString());
        sb.append("( ");
        Iterator<VarEntry> i = params.iterator();
        while (i.hasNext()) {
            VarEntry entry = i.next();
            sb.append(entry.getMode().toString() + " ");
            sb.append(entry.getName().toString());
            sb.append(": ");
            sb.append(entry.getType().toString());
            if (i.hasNext()) {
                sb.append("; ");
            }
        }
        sb.append(" ): ");
        sb.append(type.toString());
        sb.append(")");
        return sb.toString();
    }

}
