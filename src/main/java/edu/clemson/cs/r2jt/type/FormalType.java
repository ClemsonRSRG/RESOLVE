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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
/*
 * FormalType.java
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

public class FormalType extends Type {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleID id;

    private PosSymbol name;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FormalType(ModuleID id, PosSymbol name) {
        this.id = id;
        this.name = name;
    }

    // ===========================================================
    // Public Methods
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

    public Type instantiate(ScopeID sid, Binding binding) {
        return this;
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
        return this;
    }

    public String toString() {
        return getProgramName().toString();
    }

    public String asString() {
        return /*"*" +*/name.toString();
    }
}
