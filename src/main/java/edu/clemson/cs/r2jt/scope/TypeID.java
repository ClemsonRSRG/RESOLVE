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
package edu.clemson.cs.r2jt.scope;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class TypeID {

    // ===========================================================
    // Variables
    // ===========================================================

    private Symbol qualifier = null;

    private Symbol name = null;

    private int params = 0;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeID(PosSymbol qualifier, PosSymbol name, int params) {
        if (qualifier != null) {
            this.qualifier = qualifier.getSymbol();
        }
        this.name = name.getSymbol();
        this.params = params;
    }

    public TypeID(Symbol qualifier, Symbol name, int params) {
        this.qualifier = qualifier;
        this.name = name;
        this.params = params;
    }

    public TypeID(Symbol sym) {
        name = sym;
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public Symbol getQualifier() {
        return qualifier;
    }

    public Symbol getName() {
        return name;
    }

    public int getParamCount() {
        return params;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (qualifier != null) {
            sb.append(qualifier.toString() + ".");
        }
        sb.append(name.toString());
        if (params > 0) {
            sb.append("(" + params + ")");
        }
        return sb.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof TypeID) {
            TypeID tid = (TypeID) obj;
            return (this.qualifier == tid.qualifier && this.name == tid.name && this.params == tid.params);
        }
        else {
            return false;
        }
    }
}
