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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;

/**
 * <p>A syntactic type based on an arbitrary mathematical <code>Exp</code>.  All
 * math expressions should have this <code>Ty</code>.  Ultimately their 
 * interfaces should be changed to reflect this fact, or this class should
 * be unwrapped and math types should simply be represented by 
 * <code>Exp</code>s.</p>
 */
public class ArbitraryExpTy extends Ty {

    private final Exp myArbitraryExp;

    public ArbitraryExpTy(Exp arbitraryExp) {
        myArbitraryExp = arbitraryExp;
    }

    public Exp getArbitraryExp() {
        return myArbitraryExp;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        v.visitArbitraryExpTy(this);
    }

    @Override
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getArbitraryExpType(this);
    }

    @Override
    public String asString(int indent, int increment) {
        return myArbitraryExp.toString();
    }

    @Override
    public String toString() {
        return myArbitraryExp.toString();
    }

    public Location getLocation() {
        return myArbitraryExp.getLocation();
    }

    @Override
    public ArbitraryExpTy copy() {
        return new ArbitraryExpTy(Exp.copy(myArbitraryExp));
    }
}
