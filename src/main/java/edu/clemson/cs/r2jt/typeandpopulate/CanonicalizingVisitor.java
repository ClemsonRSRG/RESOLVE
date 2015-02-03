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

import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.typereasoning.EqualsPredicate;
import edu.clemson.cs.r2jt.typereasoning.IsInPredicate;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.typereasoning.TypeRelationshipPredicate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class CanonicalizingVisitor extends MutatingVisitor {

    private int myQuantifiedVariableCount = 1;

    private final Map<String, String> myCanonicalToEnvironmentOriginal =
            new HashMap<String, String>();

    private final List<TypeRelationshipPredicate> myPredicates =
            new LinkedList<TypeRelationshipPredicate>();

    private MTType myRoot;

    private boolean mySpentFlag = false;

    private final String myPredicateSuffix;

    private final TypeGraph myTypeGraph;
    private final Scope myEnvironment;
    private final Map<SymbolTableEntry, Map<Object, Object>> myEnvironmentAnnotations =
            new HashMap<SymbolTableEntry, Map<Object, Object>>();

    public CanonicalizingVisitor(TypeGraph g, Scope environment,
            String predicateSuffix) {

        myTypeGraph = g;
        myPredicateSuffix = "_" + predicateSuffix;
        myEnvironment = environment;
    }

    public List<TypeRelationshipPredicate> getTypePredicates() {
        return Collections.unmodifiableList(myPredicates);
    }

    public Map<String, String> getCanonicalToEnvironmentOriginalMapping() {
        return Collections.unmodifiableMap(myCanonicalToEnvironmentOriginal);
    }

    @Override
    public void mutateBeginMTType(MTType t) {
        if (mySpentFlag) {
            throw new IllegalStateException("Cannot reuse a " + this.getClass()
                    + ".  Make a new one.");
        }

        if (myRoot == null) {
            myRoot = t;
        }
    }

    @Override
    public void mutateEndMTType(MTType t) {
        if (atRoot()) {
            //Note at this point that myFinalExpression has no big unions and 
            //unique quantified variable names

            Map<String, MTType> quantifiedVariables =
                    new HashMap<String, MTType>();

            for (int i = 1; i < myQuantifiedVariableCount; i++) {
                quantifiedVariables.put("c" + i, myTypeGraph.MTYPE);
            }

            if (quantifiedVariables.isEmpty()) {
                quantifiedVariables.put("", myTypeGraph.MTYPE);
            }

            myFinalExpression =
                    new MTBigUnion(myTypeGraph, quantifiedVariables,
                            myFinalExpression);

            myRoot = null;

            mySpentFlag = true;
        }
    }

    @Override
    public void beginMTNamed(MTNamed t) {
        String canonicalName = "c" + myQuantifiedVariableCount;

        MTType originalBinding = null;
        try {
            originalBinding = getInnermostBinding(t.name);
        }
        catch (NoSuchElementException e) {
            //The variable is unbound.  Must be in the environment
            try {
                //We cast rather than call toMathSymbolEntry() because this 
                //would represent an error in the compiler code rather than the
                //RESOLVE source: you shouldn't be able to canonicalize anything
                //containing non-math-symbol pieces
                MathSymbolEntry entry =
                        (MathSymbolEntry) myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(t.name));

                if (entry.getQuantification().equals(
                        SymbolTableEntry.Quantification.UNIVERSAL)) {
                    originalBinding = entry.getType();
                    myCanonicalToEnvironmentOriginal.put(canonicalName
                            + myPredicateSuffix, t.name);
                }
            }
            catch (NoSuchSymbolException nsse) {
                //Shouldn't be possible--we'd've noticed it before now
                throw new RuntimeException(nsse);
            }
            catch (DuplicateSymbolException dse) {
                //Shouldn't be possible--we're in the context of a math 
                //expression
                throw new RuntimeException(dse);
            }
        }

        //if originalBinding is null, then we name some 
        //non-universally-quantified thing, so we do nothing
        if (originalBinding != null) {
            //Construct a canonical variable to represent this thing we've just
            //encountered
            MTNamed canonicalType = new MTNamed(myTypeGraph, canonicalName);
            MTNamed canonicalTypeWithSuffix =
                    new MTNamed(myTypeGraph, canonicalName + myPredicateSuffix);
            myQuantifiedVariableCount++;

            //We're going to weaken it's declared type all the way to MType, so
            //make a note of its original declared type
            myPredicates.add(new IsInPredicate(myTypeGraph,
                    canonicalTypeWithSuffix, originalBinding));

            //If we've already encountered this particular variable, we're going
            //to remove that information as we canonicalize, so we make a note
            //that this new canonical variable should equal the last one
            MTNamed lastReplace =
                    (MTNamed) getInnermostBindingAnnotation(t.name,
                            "LastReplace");

            if (lastReplace != null) {
                myPredicates.add(new EqualsPredicate(myTypeGraph, lastReplace,
                        canonicalTypeWithSuffix));
            }
            annotateInnermostBinding(t.name, "LastReplace",
                    canonicalTypeWithSuffix);

            //Replace "t" with "canonical" in the final expression
            replaceWith(canonicalType);
        }
    }

    @Override
    public void annotateInnermostBinding(String name, Object key, Object value) {

        try {
            super.annotateInnermostBinding(name, key, value);
        }
        catch (NoSuchElementException e) {
            try {
                SymbolTableEntry entry =
                        myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(name));

                Map<Object, Object> annotations =
                        myEnvironmentAnnotations.get(entry);
                if (annotations == null) {
                    annotations = new HashMap<Object, Object>();
                    myEnvironmentAnnotations.put(entry, annotations);
                }
                annotations.put(key, value);
            }
            catch (NoSuchSymbolException nsse) {
                throw new NoSuchElementException(name);
            }
            catch (DuplicateSymbolException dse) {
                //Shouldn't be possible--the query returns exactly one match
                throw new RuntimeException(dse);
            }
        }
    }

    @Override
    public Object getInnermostBindingAnnotation(String name, Object key) {
        Object result;

        try {
            result = super.getInnermostBindingAnnotation(name, key);
        }
        catch (NoSuchElementException e) {
            try {
                SymbolTableEntry entry =
                        myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(name));

                Map<Object, Object> annotations =
                        myEnvironmentAnnotations.get(entry);
                if (annotations == null) {
                    result = null;
                }
                else {
                    result = annotations.get(key);
                }
            }
            catch (NoSuchSymbolException nsse) {
                throw new NoSuchElementException(name);
            }
            catch (DuplicateSymbolException dse) {
                //Shouldn't be possible--the query returns exactly one match
                throw new RuntimeException(dse);
            }
        }

        return result;
    }

    @Override
    public void boundBeginMTBigUnion(MTBigUnion t) {

    }

    @Override
    public void boundEndMTBigUnion(MTBigUnion t) {
        //We better have encountered each quantified type explicitly
        for (String var : t.getQuantifiedVariables().keySet()) {
            Object lastReplace =
                    getInnermostBindingAnnotation(var, "LastReplace");

            if (lastReplace == null) {
                //TODO : Ideally this exception would be semantically connected
                //       to the location in the RESOLVE source code that caused 
                //       the error so we could give nice error output
                throw new IllegalArgumentException("Universal type variable "
                        + "\"" + var + "\" is not concretely bound.");
            }
        }

        //We're gonna get rid of all intermediate big unions and just have one
        //big one at the top
        replaceWith(((MTBigUnion) getTransformedVersion()).getExpression());
    }
}
