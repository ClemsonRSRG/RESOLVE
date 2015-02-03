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
package edu.clemson.cs.r2jt.proving;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>TotalBindingIterator</code> returns successive possible 
 * bindings of a set of universally quantified <code>Exp</code>s to a set of 
 * available facts, possibly given a set of assumed bindings.  All possible 
 * bindings that successfully bind all contained universal variables will 
 * be returned, but the order they will be returned in is undefined.</p>
 * 
 * <p>Note that returned bindings will include any assumed bindings established
 * in the constructor in addition to any new bindings.  Note also that, if the 
 * set of universally quantified <code>Exp</code>s to bind is empty, this class 
 * will return a single binding--the null binding--which will contain only the
 * assumed bindings.</p>
 * 
 * <p>Note that by time the set of <code>Exp</code>s make their way to this 
 * class, quantifiers should already have been eliminated and propagated down to
 * the variables themselves (that is, the variable nodes should reflect their 
 * quantified state, not a top-level quantifying expression.)</p>
 */
public class TotalBindingIterator implements Iterator<Map<PExp, PExp>> {

    private final Iterable<PExp> myFacts;
    private final Map<PExp, PExp> myAssumedBindings;

    private Map<PExp, PExp> myNextBinding;

    private final Iterator<Map<PExp, PExp>> myLocalPatternBinder;
    private final Antecedent myRemainingPatterns;

    private Iterator<Map<PExp, PExp>> myRemainderBindings;

    public TotalBindingIterator(Antecedent patterns, Iterable<PExp> facts,
            Map<PExp, PExp> assumedBindings) {

        myFacts = facts;
        myAssumedBindings = assumedBindings;

        if (patterns.size() > 0) {
            myLocalPatternBinder =
                    new IncrementalBindingIterator(patterns.get(0), myFacts
                            .iterator(), myAssumedBindings);
            myRemainingPatterns = patterns.subConjuncts(1, patterns.size());
        }
        else {
            myLocalPatternBinder =
                    new SingletonIterator<Map<PExp, PExp>>(assumedBindings);
            myRemainingPatterns = null;
        }

        setUpNext();
    }

    private void setUpNext() {

        myRemainderBindings = DummyIterator.getInstance(myRemainderBindings);

        while (myLocalPatternBinder.hasNext() && !myRemainderBindings.hasNext()) {

            Map<PExp, PExp> nextLocalBinding = myLocalPatternBinder.next();
            Map<PExp, PExp> unifiedBindings =
                    unifyMaps(nextLocalBinding, myAssumedBindings);

            if (myRemainingPatterns != null) {
                myRemainderBindings =
                        new TotalBindingIterator(myRemainingPatterns, myFacts,
                                unifiedBindings);
            }
            else {
                myRemainderBindings =
                        new SingletonIterator<Map<PExp, PExp>>(unifiedBindings);
            }
        }

        if (myRemainderBindings.hasNext()) {
            myNextBinding = myRemainderBindings.next();
        }
        else {
            myNextBinding = null;
        }
    }

    private static <K, V> Map<K, V> unifyMaps(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> retval = new HashMap<K, V>();

        retval.putAll(map1);
        retval.putAll(map2);

        return retval;
    }

    @Override
    public boolean hasNext() {
        return myNextBinding != null;
    }

    @Override
    public Map<PExp, PExp> next() {

        Map<PExp, PExp> retval = myNextBinding;

        setUpNext();

        return retval;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
