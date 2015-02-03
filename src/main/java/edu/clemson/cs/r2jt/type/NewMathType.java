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
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

/**
 * <p>This class attempts to wrap one of the new types from the 
 * <code>MTType</code> hierarchy so that it can be used by the old type system.
 * This class simply exists to ease the transition from old type system to new.
 * Ultimately there should be no need for this class--nobody will use the old
 * type system.</p>
 */
public class NewMathType extends Type {

    private final MTType myMTType;

    public NewMathType(MTType mtType) {
        myMTType = mtType;
    }

    public MTType getWrappedType() {
        return myMTType;
    }

    @Override
    public NewMathType instantiate(ScopeID sid, Binding binding) {
        return this;
    }

    @Override
    public TypeName getProgramName() {
        throw new UnsupportedOperationException(
                "NewType doesn't support this operation.");
    }

    @Override
    public String getRelativeName(Location loc) {
        throw new UnsupportedOperationException(
                "NewType doesn't support this operation.");
    }

    @Override
    public NewMathType toMath() {
        return this;
    }

    @Override
    public String asString() {
        return "" + myMTType;
    }

    @Override
    public String toString() {
        return "" + myMTType;
    }
}
