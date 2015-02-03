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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * TODO : MutatingVisitor tends to crash if you use it for multiple sequential
 * traversals.  Always creating a new instance of the visitor is a workaround
 * for now.
 */
public class MutatingVisitor extends BoundVariableVisitor {

    private LinkedList<Integer> myIndices = new LinkedList<Integer>();
    private MTType myRoot;
    private LinkedList<Map<Integer, MTType>> myChangesAtLevel =
            new LinkedList<Map<Integer, MTType>>();

    private MTType myClosingType;

    protected MTType myFinalExpression;

    public MTType getFinalExpression() {
        return myFinalExpression;
    }

    @Override
    public final void beginMTType(MTType t) {

        if (myRoot == null) {
            myRoot = t;
            myFinalExpression = myRoot;
        }

        myIndices.push(0); //We start at the zeroth child
        myChangesAtLevel.push(new HashMap<Integer, MTType>());

        mutateBeginMTType(t);
    }

    protected boolean atRoot() {
        return (myIndices.size() == 1);
    }

    public void mutateBeginMTType(MTType t) {}

    public void mutateEndMTType(MTType t) {}

    public void replaceWith(MTType replacement) {
        if (myIndices.size() == 1) {
            //We're the root
            myFinalExpression = replacement;
        }
        else {
            myChangesAtLevel.get(1).put(myIndices.get(1), replacement);
        }
    }

    protected final MTType getTransformedVersion() {
        return myClosingType;
    }

    @Override
    public final void endChildren(MTType t) {
        myClosingType = t;

        Map<Integer, MTType> changes = myChangesAtLevel.peek();
        if (!changes.isEmpty()) {
            myClosingType = t.withComponentsReplaced(changes);
            replaceWith(myClosingType);
        }

        mutateEndChildren(t);
    }

    public void mutateEndChildren(MTType t) {}

    @Override
    public final void endMTType(MTType t) {
        mutateEndMTType(t);

        //We're not visiting any more children at this level (because the
        //level just ended!)
        myIndices.pop();
        myChangesAtLevel.pop();

        //If I'm the root, there's no chance I have any siblings
        if (t != myRoot) {
            //Increment to the next potential child index
            int i = myIndices.pop();

            myIndices.push(i + 1);
        }
    }
}
