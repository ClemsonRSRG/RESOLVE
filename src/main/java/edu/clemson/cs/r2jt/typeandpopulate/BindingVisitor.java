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
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Attempts to bind the concrete expression <code>t1</code> against the
 * template expression <code>t2</code>.</p>
 */
public class BindingVisitor extends SymmetricBoundVariableVisitor {

    private final TypeGraph myTypeGraph;
    private Map<String, MTType> myBindings = new HashMap<String, MTType>();

    private boolean myMatchSoFarFlag = true;

    public BindingVisitor(TypeGraph g) {
        myTypeGraph = g;
    }

    public BindingVisitor(TypeGraph g, FinalizedScope concreteContext) {
        super(concreteContext);
        myTypeGraph = g;
    }

    public BindingVisitor(TypeGraph g, Map<String, MTType> concreteContext) {
        super(concreteContext);
        myTypeGraph = g;
    }

    public BindingVisitor(TypeGraph g, Map<String, MTType> concreteContext,
            Map<String, MTType> templateContext) {
        super(concreteContext, templateContext);
        myTypeGraph = g;
    }

    public boolean binds() {
        return myMatchSoFarFlag;
    }

    public Map<String, MTType> getBindings() {
        return myBindings;
    }

    @Override
    public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        MTType t1DeclaredType = getInnermostBinding1(t1.name);
        MTType t2DeclaredType = getInnermostBinding2(t2.name);

        if (myBindings.containsKey(t2.name)) {
            t1DeclaredType = myBindings.get(t2.name);
        }

        //Fine if the declared type of t1 restricts the declared type of t2
        myMatchSoFarFlag &=
                myTypeGraph.isSubtype(t1DeclaredType, t2DeclaredType);

        if (!myBindings.containsKey(t2.name) && myMatchSoFarFlag) {
            myBindings.put(t2.name, t1);
        }

        //No need to keep searching if we've already found we don't bind
        return myMatchSoFarFlag;
    }

    @Override
    public boolean beginMTProper(MTProper t1, MTProper t2) {
        myMatchSoFarFlag &= t1.equals(t2);

        //No need to keep searching if we've already found we don't bind
        return myMatchSoFarFlag;
    }

    @Override
    public boolean mismatch(MTType t1, MTType t2) {

        //This is fine if t1 names a type of which t2 is a supertype
        if (t2 instanceof MTNamed) {
            String t2Name = ((MTNamed) t2).name;
            MTType t2DeclaredType = getInnermostBinding2(t2Name);

            if (myBindings.containsKey(((MTNamed) t2).name)) {
                t1 = myBindings.get(((MTNamed) t2).name);
            }

            myMatchSoFarFlag &= myTypeGraph.isSubtype(t1, t2DeclaredType);

            if (!myBindings.containsKey(((MTNamed) t2).name)
                    && myMatchSoFarFlag) {
                myBindings.put(t2Name, t1);
            }
        }
        else if (t1 instanceof MTBigUnion) {
            //So long as the inner expression binds, this is ok
            myMatchSoFarFlag = visit(((MTBigUnion) t1).getExpression(), t2);
        }
        else if (t2 instanceof MTBigUnion) {
            //So long as the inner expression binds, this is ok
            myMatchSoFarFlag = visit(t1, ((MTBigUnion) t2).getExpression());
        }
        else {
            myMatchSoFarFlag = false;
        }

        //No need to keep searching if we've already found we don't bind
        return myMatchSoFarFlag;
    }
}
