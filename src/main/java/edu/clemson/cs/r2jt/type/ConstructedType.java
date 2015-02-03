/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
