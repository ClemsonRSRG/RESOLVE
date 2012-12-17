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
        } else {
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


