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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.IterativeExp;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.ArrayList;

/**
 * <p>A constructed type consisting of the union over one or more quantified
 * types.  For example U{t, r : MType}{t intersect r} is the type of all 
 * intersections.</p>
 */
public class MTBigUnion extends MTAbstract<MTBigUnion> {

    private static final int BASE_HASH = "MTBigUnion".hashCode();

    private TreeMap<String, MTType> myQuantifiedVariables;

    /**
     * If <code>myQuantifiedVariables</code> is <code>null</code>, then
     * <code>myUniqueQuantifiedVariableCount</code> is undefined.
     */
    private final int myUniqueQuantifiedVariableCount;

    private final MTType myExpression;

    private final Map<Integer, String> myComponentIndecis =
            new HashMap<Integer, String>();
    private List<MTType> myComponents;

    public MTBigUnion(TypeGraph g, Map<String, MTType> quantifiedVariables,
            MTType expression) {
        super(g);

        myQuantifiedVariables =
                new TreeMap<String, MTType>(quantifiedVariables);
        myUniqueQuantifiedVariableCount = -1;
        myExpression = expression;
    }

    /**
     * <p>This provides a small optimization for working with 
     * {@link SyntacticSubtypeChecker SyntacticSubtypeChecker}.  In the case
     * where we're just going to have <em>n</em> variables whose names are
     * meant to be guaranteed not to appear in <code>expression</code>, we just
     * pass in the number of variables this union is meant to be quantified over
     * rather than going through the trouble of giving them names and types and
     * putting them in a map.</p>
     * @param g
     * @param uniqueVariableCount
     * @param expression 
     */
    MTBigUnion(TypeGraph g, int uniqueVariableCount, MTType expression) {
        super(g);

        myQuantifiedVariables = null;
        myUniqueQuantifiedVariableCount = uniqueVariableCount;
        myExpression = expression;
    }

    public MTType getExpression() {
        return myExpression;
    }

    public int getQuantifiedVariablesSize() {
        int result;

        if (myQuantifiedVariables == null) {
            result = myUniqueQuantifiedVariableCount;
        }
        else {
            result = myQuantifiedVariables.size();
        }

        return result;
    }

    public Map<String, MTType> getQuantifiedVariables() {
        ensureQuantifiedTypes();

        return myQuantifiedVariables;
    }

    @Override
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTBigUnion(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        if (myQuantifiedVariables == null) {
            for (int i = 0; i < myUniqueQuantifiedVariableCount; i++) {
                myTypeGraph.MTYPE.accept(v);
            }
        }
        else {
            for (MTType t : myQuantifiedVariables.values()) {
                t.accept(v);
            }
        }

        myExpression.accept(v);

        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
        v.endMTBigUnion(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        if (myComponents == null) {
            if (myQuantifiedVariables == null) {
                myComponents =
                        new ArrayList<MTType>(myUniqueQuantifiedVariableCount);

                for (int i = 0; i < myUniqueQuantifiedVariableCount; i++) {
                    myComponents.add(myTypeGraph.MTYPE);
                }
            }
            else {
                List<MTType> components =
                        new ArrayList<MTType>(myQuantifiedVariables.size());
                for (Map.Entry<String, MTType> entry : myQuantifiedVariables
                        .entrySet()) {

                    myComponentIndecis.put(components.size(), entry.getKey());
                    components.add(entry.getValue());
                }
                components.add(myExpression);
                myComponents = Collections.unmodifiableList(components);
            }
        }

        return myComponents;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        ensureQuantifiedTypes();

        Map<String, MTType> newQuantifiedVariables;
        MTType newExpression;

        if (index < myQuantifiedVariables.size()) {
            newQuantifiedVariables =
                    new HashMap<String, MTType>(myQuantifiedVariables);

            newQuantifiedVariables.put(myComponentIndecis.get(index), newType);

            newExpression = myExpression;
        }
        else if (index == myQuantifiedVariables.size()) {
            newQuantifiedVariables = myQuantifiedVariables;

            newExpression = newType;
        }
        else {
            throw new IndexOutOfBoundsException();
        }

        return new MTBigUnion(getTypeGraph(), newQuantifiedVariables,
                newExpression);
    }

    @Override
    public int getHashCode() {
        ensureQuantifiedTypes();

        int result = BASE_HASH;

        //Note that order of these MTTypes doesn't matter
        for (MTType t : myQuantifiedVariables.values()) {
            result += t.hashCode();
        }

        result *= 57;
        result += myExpression.hashCode();

        return result;
    }

    @Override
    public String toString() {
        ensureQuantifiedTypes();

        return "BigUnion" + myQuantifiedVariables + "{" + myExpression + "}";
    }

    /**
     * <p>Converts us from a "enh, some number of unique variables" big union to
     * a "specific named unique variables" big union if one of the methods is
     * called that requires such a thing.</p>
     */
    private void ensureQuantifiedTypes() {
        if (myQuantifiedVariables == null) {
            myQuantifiedVariables = new TreeMap<String, MTType>();

            for (int i = 0; i < myUniqueQuantifiedVariableCount; i++) {
                myQuantifiedVariables.put("*" + i, myTypeGraph.MTYPE);
            }
        }
    }
}
