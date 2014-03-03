/**
 * ArrayType.java
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

import edu.clemson.cs.r2jt.absyn.ProgramExp;
import edu.clemson.cs.r2jt.absyn.ProgramIntegerExp;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class ArrayType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleID id;

    private PosSymbol name;

    private ProgramExp lo;

    private ProgramExp hi;

    private Type index;

    private Type entry;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ArrayType(ModuleID id, PosSymbol name, ProgramExp lo, ProgramExp hi,
            Type index, Type entry) {
        this.id = id;
        this.name = name;
        this.lo = lo;
        this.hi = hi;
        this.index = index;
        this.entry = entry;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public ModuleID getModuleID() {
        return id;
    }

    public PosSymbol getName() {
        return name;
    }

    public void setName(PosSymbol name) {
        this.name = name;
    }

    public ProgramExp getHi() {
        return hi;
    }

    public ProgramExp getLo() {
        return lo;
    }

    public Type getIndex() {
        return index;
    }

    public Type getEntry() {
        return entry;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type instantiate(ScopeID sid, Binding binding) {
        return new ArrayType(id, name, lo, hi, index.instantiate(sid, binding),
                entry.instantiate(sid, binding));
    }

    public TypeName getProgramName() {
        return new TypeName(id, null, name);
    }

    public String getRelativeName(Location loc) {
        StringBuffer sb = new StringBuffer();
        if (name.toString().startsWith("%")) {
            sb.append("Array declared at ");
            sb.append(name.getPos().toString());
            if (!(loc.getFilename().equals(id.getFilename()))) {
                sb.append(" in " + id.toString());
            }
        }
        else {
            if (!(loc.getFilename().equals(id.getFilename()))) {
                sb.append(id.toString() + ".");
            }
            sb.append(name.toString());
        }
        return sb.toString();
    }

    public Type toMath() {
        return new FunctionType(index.toMath(), entry.toMath());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ARRAY]");
        sb.append(getProgramName().toString());
        sb.append("(");
        sb.append(index.toString());
        sb.append("[");
        if (lo instanceof ProgramIntegerExp) {
            sb.append(((ProgramIntegerExp) lo).getValue());
        }
        else {
            sb.append("exp");
        }
        sb.append("..");
        if (hi instanceof ProgramIntegerExp) {
            sb.append(((ProgramIntegerExp) hi).getValue());
        }
        else {
            sb.append("exp");
        }
        sb.append("]");
        sb.append(" -> ");
        sb.append(entry.toString());
        sb.append(")");
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ARRAY]");
        sb.append(getProgramName().toString());
        sb.append("(");
        sb.append(index.toString());
        sb.append("[");
        if (lo instanceof ProgramIntegerExp) {
            sb.append(((ProgramIntegerExp) lo).getValue());
        }
        else {
            sb.append("exp");
        }
        sb.append("..");
        if (hi instanceof ProgramIntegerExp) {
            sb.append(((ProgramIntegerExp) hi).getValue());
        }
        else {
            sb.append("exp");
        }
        sb.append("]");
        sb.append(" -> ");
        sb.append(entry.toString());
        sb.append(")");
        return sb.toString();
    }
}
