/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */

/*
 * ArrayType.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
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

    public ArrayType(ModuleID id, PosSymbol name, ProgramExp lo,
                     ProgramExp hi, Type index, Type entry) {
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
    
    public ModuleID getModuleID() { return id; }
    
    public PosSymbol getName() { return name; }

    public void setName(PosSymbol name) { this.name = name; }

    public ProgramExp getHi() { return hi; }

    public ProgramExp getLo() { return lo; }

    public Type getIndex() { return index; }

    public Type getEntry() { return entry; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type instantiate(ScopeID sid, Binding binding) {
        return new ArrayType(id, name, lo, hi,
                             index.instantiate(sid, binding),
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
        } else {
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
            sb.append(((ProgramIntegerExp)lo).getValue());
        } else {
            sb.append("exp");
        }
        sb.append("..");
        if (hi instanceof ProgramIntegerExp) {
            sb.append(((ProgramIntegerExp)hi).getValue());
        } else {
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
            sb.append(((ProgramIntegerExp)lo).getValue());
        } else {
            sb.append("exp");
        }
        sb.append("..");
        if (hi instanceof ProgramIntegerExp) {
            sb.append(((ProgramIntegerExp)hi).getValue());
        } else {
            sb.append("exp");
        }
        sb.append("]");
        sb.append(" -> ");
        sb.append(entry.toString());
        sb.append(")");
        return sb.toString();
    }
}
