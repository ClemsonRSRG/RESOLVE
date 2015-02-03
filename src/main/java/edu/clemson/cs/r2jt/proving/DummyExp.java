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
package edu.clemson.cs.r2jt.proving;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualVisitor;
import edu.clemson.cs.r2jt.absyn.TypeResolutionVisitor;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;

/**
 * A simple Exp to hack around a bug quickly.  MatchApplicator should be fixed
 * so that this is not necessary.
 * 
 * @author H. Smith
 *
 */
public class DummyExp extends Exp {

    private Exp myWrappedExpression;

    public DummyExp(Exp e) {
        myWrappedExpression = e;
    }

    public DummyExp() {
        myWrappedExpression = null;
    }

    public Exp getWrappedExpression() {
        return myWrappedExpression;
    }

    public void setWrappedExpression(Exp e) {
        myWrappedExpression = e;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
    // TODO Auto-generated method stub

    }

    @Override
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String asString(int indent, int increment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Location getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();

        if (myWrappedExpression != null) {
            list.add(myWrappedExpression);
        }

        return list;
    }

    @Override
    public void setSubExpression(int index, Exp e) {
        myWrappedExpression = e;
    }

    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        // TODO Auto-generated method stub
        return null;
    }

}
