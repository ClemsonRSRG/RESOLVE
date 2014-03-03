/**
 * IndirectType.java
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
 * IndirectType.java
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
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class IndirectType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    //private Environment env = Environment.getInstance();

    private PosSymbol qualifier;

    private PosSymbol name;

    private Binding binding;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IndirectType(PosSymbol qualifier, PosSymbol name, Binding binding) {
        this.qualifier = qualifier;
        this.name = name;
        this.binding = binding;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public PosSymbol getName() {
        return name;
    }

    public PosSymbol getQualifier() {
        return qualifier;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type instantiate(ScopeID sid, Binding replBind) {
        if (binding.getScopeID().equals(sid)) {
            return new IndirectType(qualifier, name, replBind);
        }
        else {
            return this;
            //return new IndirectType(qualifier, name, binding);
        }
    }

    public TypeName getProgramName() {
        return binding.getProgramName(qualifier, name);
    }

    public String getRelativeName(Location loc) {
        return binding.getType(qualifier, name).getRelativeName(loc);
    }

    public Type getType() {
        return binding.getType(qualifier, name);
    }

    public Type toMath() {
        return binding.toMath(qualifier, name);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("*");
        if (qualifier != null) {
            sb.append(qualifier.toString() + ".");
        }
        sb.append(name.toString());
        /*if (env.showIndirect()) {
            sb.append("{" + binding.getScopeID().toString() + "}");
        }*/
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        //    sb.append("*");
        if (qualifier != null) {
            sb.append(qualifier.toString() + ".");
        }
        sb.append(name.toString());
        /*if (env.showIndirect()) {
            sb.append("{" + binding.getScopeID().toString() + "}");
        }*/
        return sb.toString();
    }
}
