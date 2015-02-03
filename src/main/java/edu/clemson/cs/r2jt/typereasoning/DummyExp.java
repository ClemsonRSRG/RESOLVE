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
package edu.clemson.cs.r2jt.typereasoning;

import java.util.Collections;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualVisitor;
import edu.clemson.cs.r2jt.absyn.TypeResolutionVisitor;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.type.Type;

/**
 * <p>A <code>DummyExp</code> is an <code>Exp</code> guaranteed not to arise
 * from any actual RESOLVE source code.  Its only property is that it has an
 * <code>MTType</code>.  It can be bound normally to quantified variables whose
 * declared type it inhabits (i.e., a <code>DummyExp</code> of type 
 * <code>N</code> is acceptable for a quantified variable of type 
 * <code>Z</code>,) but nothing will bind to it.</p>
 * 
 * <p>Mostly useful for representing "a unique variable of type X" without 
 * having to worry if its name is truly unique.</p>
 */
public class DummyExp extends Exp {

    public DummyExp(MTType t) {
        myMathType = t;
    }

    public DummyExp(DummyExp original) {
        myMathType = original.myMathType;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        throw new UnsupportedOperationException("Cannot visit a DummyExp.");
    }

    @Override
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        throw new UnsupportedOperationException("Cannot visit a DummyExp.");
    }

    @Override
    public String asString(int indent, int increment) {
        return "" + this.getClass();
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Exp> getSubExpressions() {
        return (List<Exp>) Collections.EMPTY_LIST;
    }

    @Override
    public void setSubExpression(int index, Exp e) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new DummyExp(this);
    }

    @Override
    public String toString() {
        return "(some " + getMathType() + ")";
    }
}
