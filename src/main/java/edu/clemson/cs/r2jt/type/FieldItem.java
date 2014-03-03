/**
 * FieldItem.java
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
 * FieldItem.java
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
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class FieldItem extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private PosSymbol name;

    private Type type;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FieldItem(PosSymbol name, Type type) {
        this.name = name;
        this.type = type;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public PosSymbol getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Type instantiate(ScopeID sid, Binding binding) {
        return new FieldItem(name, type.instantiate(sid, binding));
    }

    public TypeName getProgramName() {
        return type.getProgramName();
    }

    public String getRelativeName(Location loc) {
        return type.getRelativeName(loc);
    }

    public Type toMath() {
        return new FieldItem(name, type.toMath());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (name != null) {
            sb.append(name.toString() + ":");
        }
        sb.append(type.toString());
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        if (name != null) {
            sb.append(name.toString() + ":");
        }
        sb.append(type.asString());
        return sb.toString();
    }
}
