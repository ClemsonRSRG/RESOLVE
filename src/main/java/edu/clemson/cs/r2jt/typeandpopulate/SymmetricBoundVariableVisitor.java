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

import edu.clemson.cs.r2jt.typeandpopulate.query.UniversalVariableQuery;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class SymmetricBoundVariableVisitor extends SymmetricVisitor {

    private Deque<Map<String, MTType>> myBoundVariables1 =
            new LinkedList<Map<String, MTType>>();

    private Deque<Map<String, MTType>> myBoundVariables2 =
            new LinkedList<Map<String, MTType>>();

    public SymmetricBoundVariableVisitor() {

    }

    public SymmetricBoundVariableVisitor(FinalizedScope context1) {
        Map<String, MTType> topLevel = new HashMap<String, MTType>();

        List<MathSymbolEntry> quantifiedVariables =
                context1.query(UniversalVariableQuery.INSTANCE);
        for (MathSymbolEntry entry : quantifiedVariables) {
            topLevel.put(entry.getName(), entry.getType());
        }

        myBoundVariables1.push(topLevel);
    }

    public SymmetricBoundVariableVisitor(Map<String, MTType> context1) {
        myBoundVariables1.push(new HashMap<String, MTType>(context1));
    }

    public SymmetricBoundVariableVisitor(Map<String, MTType> context1,
            Map<String, MTType> context2) {
        this(context1);
        myBoundVariables2.push(new HashMap<String, MTType>(context2));
    }

    public void reset() {
        myBoundVariables1.clear();
        myBoundVariables2.clear();
    }

    public final boolean beginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        myBoundVariables1.push(t1.getQuantifiedVariables());
        myBoundVariables2.push(t2.getQuantifiedVariables());
        return boundBeginMTBigUnion(t1, t2);
    }

    public final boolean endMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        boolean result = boundEndMTBigUnion(t1, t2);
        myBoundVariables1.pop();
        myBoundVariables2.pop();

        return result;
    }

    public boolean boundBeginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    public boolean boundEndMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    public MTType getInnermostBinding1(String name) {
        return getInnermostBinding(myBoundVariables1, name);
    }

    public MTType getInnermostBinding2(String name) {
        return getInnermostBinding(myBoundVariables2, name);
    }

    private static MTType getInnermostBinding(
            Deque<Map<String, MTType>> scopes, String name)
            throws NoSuchElementException {

        MTType result = null;

        Iterator<Map<String, MTType>> scopesIter = scopes.iterator();
        while (result == null && scopesIter.hasNext()) {
            result = scopesIter.next().get(name);
        }

        if (result == null) {
            throw new NoSuchElementException(name);
        }

        return result;
    }
}
