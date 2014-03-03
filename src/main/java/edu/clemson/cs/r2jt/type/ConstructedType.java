/**
 * ConstructedType.java
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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class ConstructedType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private PosSymbol qualifier;

    private PosSymbol name;

    private List<Type> args = new List<Type>();

    private Binding binding;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ConstructedType(PosSymbol qualifier, PosSymbol name,
            List<Type> args, Binding binding) {
        this.qualifier = qualifier;
        this.name = name;
        this.args.addAll(args);
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

    public List<Type> getArgs() {
        return args;
    }

    public void setArgs(List<Type> newArgs) {
        args = newArgs;
    }

    public void setQualifier(PosSymbol newQualifier) {
        qualifier = newQualifier;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public ConstructedType instantiate(ScopeID sid, Binding replBind) {
        List<Type> args2 = new List<Type>();
        Iterator<Type> i = args.iterator();
        while (i.hasNext()) {
            Type type = i.next();
            args2.add(type.instantiate(sid, replBind));
        }
        if (binding.getScopeID().equals(sid)) {
            return new ConstructedType(qualifier, name, args2, replBind);
        }
        else {
            return new ConstructedType(qualifier, name, args2, binding);
        }
    }

    public TypeName getProgramName() {
        return binding.getProgramName(qualifier, name);
    }

    public String getRelativeName(Location loc) {
        return null;
    }

    public ConstructedType toMath() {
        if (qualifier == null) {
            if (binding.getQualifier(name, args.size()) != null) {
                qualifier =
                        new PosSymbol(name.getLocation(), binding.getQualifier(
                                name, args.size()));
            }
        }
        List<Type> args2 = new List<Type>();
        Iterator<Type> i = args.iterator();
        while (i.hasNext()) {
            Type type = i.next();
            args2.add(type.toMath());
        }
        return new ConstructedType(qualifier, name, args2, binding);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("*");
        if (qualifier != null) {
            sb.append(qualifier.toString());
            sb.append(".");
        }
        sb.append(name.toString());
        sb.append("(");
        Iterator<Type> i = args.iterator();
        while (i.hasNext()) {
            Type type = i.next();
            sb.append(type.toString());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        //   sb.append("*");
        if (qualifier != null) {
            sb.append(qualifier.toString());
            sb.append(".");
        }
        sb.append(name.toString());
        sb.append("(");
        Iterator<Type> i = args.iterator();
        while (i.hasNext()) {
            Type type = i.next();
            sb.append(type.asString());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
