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
 * OperationEntry.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
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
    
    public int getParamNum(){
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
        return new OperationEntry(binding.getScope(), name, params2,
                                  type.instantiate(sid, binding));
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
            if (i.hasNext()) { sb.append("; "); }
        }
        sb.append(" ): ");
        sb.append(type.toString());
        sb.append(")");
        return sb.toString();
    }
        

}
