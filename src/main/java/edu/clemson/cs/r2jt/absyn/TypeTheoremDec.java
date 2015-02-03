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

import java.util.Iterator;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class TypeTheoremDec extends Dec {

    private PosSymbol myName;
    private List<MathVarDec> myUniversalVars = new List<MathVarDec>();
    private Exp myAssertion;

    public void addVarDecGroup(List<MathVarDec> vars) {
        Iterator<MathVarDec> iter = vars.iterator();
        while (iter.hasNext()) {
            myUniversalVars.add(iter.next());
        }
    }

    public void setName(PosSymbol name) {
        this.myName = name;
    }

    public void setAssertion(Exp assertion) {
        this.myAssertion = assertion;
    }

    public Exp getBindingCondition() {
        if (hasBindingCondition()) {
            return ((InfixExp) myAssertion).getLeft();
        }
        return null;
    }

    public Exp getBindingExpression() {
        if (hasBindingCondition()) {
            return ((InfixExp) myAssertion).getRight();
        }
        return myAssertion;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
    // don't need this with the new walker
    }

    @Override
    public String asString(int indent, int increment) {
        return "Type Theorem " + myName;
    }

    @Override
    public PosSymbol getName() {
        return myName;
    }

    public List<MathVarDec> getUniversalVars() {
        return myUniversalVars;
    }

    public Exp getAssertion() {
        return myAssertion;
    }

    public boolean hasBindingCondition() {
        return myAssertion instanceof InfixExp
                && ((InfixExp) myAssertion).getOperatorAsString().equals(
                        "implies");
    }
}
