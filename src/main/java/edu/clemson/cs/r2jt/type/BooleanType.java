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

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class BooleanType extends Type {

    public static final BooleanType INSTANCE = new BooleanType();

    // ===========================================================
    // Variables
    // ===========================================================

    //private ModuleID id = ModuleID.createTheoryID(Symbol.symbol("Boolean"));

    private PosSymbol myQualifier = new PosSymbol();

    private PosSymbol myName = new PosSymbol();

    private BooleanType() {
        myQualifier.setSymbol(Symbol.symbol("Boolean"));
        myName.setSymbol(Symbol.symbol("B"));
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public PosSymbol getName() {
        return myName;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Type instantiate(ScopeID sid, Binding replBind) {
        return new BooleanType();
    }

    public Type toMath() {
        return new BooleanType();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("*");
        if (myQualifier != null) {
            sb.append(myQualifier.toString() + ".");
        }
        sb.append(myName.toString());
        return sb.toString();
    }

    public String asString() {
        StringBuffer sb = new StringBuffer();
        //    sb.append("*");
        if (myQualifier != null) {
            sb.append(myQualifier.toString() + ".");
        }
        sb.append(myName.toString());
        return sb.toString();
    }

    @Override
    public TypeName getProgramName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRelativeName(Location loc) {
        // TODO Auto-generated method stub
        return null;
    }
}
