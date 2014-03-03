/**
 * ConcType.java
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
 * ConcType.java
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
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class ConcType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleID id;

    private PosSymbol name;

    private Type type;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ConcType(ModuleID id, PosSymbol name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public PosSymbol getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public ModuleID getModuleID() {
        return id;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void setName(PosSymbol name) {
        this.name = name;
    }

    public Type instantiate(ScopeID sid, Binding binding) {
        return new ConcType(id, name, type.instantiate(sid, binding));
    }

    public TypeName getProgramName() {
        return new TypeName(id, null, name);
    }

    public String getRelativeName(Location loc) {
        StringBuffer sb = new StringBuffer();
        if (!(loc.getFilename().equals(id.getFilename()))) {
            sb.append(id.toString() + ".");
        }
        sb.append(name.getSymbol().toString());
        return sb.toString();
    }

    public Type toMath() {
        return type.toMath();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[CONC]");
        sb.append(getProgramName().toString());
        sb.append("{" + type.toString() + "}");
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getProgramName().toString());
        sb.append("{" + type.asString() + "}");
        return sb.toString();
    }

    public Object clone() {
        return new ConcType(this.id, this.name, this.type);
    }

}
