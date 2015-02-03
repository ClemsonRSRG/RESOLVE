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

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>ConjunctGranularityBindingIterator</code> takes a pattern set of
 * universally quantified conjuncts and a target set of conjuncts, and attempts 
 * to bind each conjunct in the pattern against some conjunct in the target, 
 * iterating over the resulting variable bindings and the set of target 
 * conjuncts that were not matched against.</p>
 */
public class ConjunctGranularityBindingIterator
        implements
            Iterator<ConjunctGranularityBindingIterator.BindingsAndRemainingConjuncts> {

    private static final Map<PExp, PExp> EMPTY_BINDINGS =
            new HashMap<PExp, PExp>();

    private final PExp myLocalPattern;
    private final ImmutableConjuncts myRemainingPattern;

    private final ImmutableConjuncts myTarget;
    private final int myTargetSize;

    private int myLocalTargetConjunctIndex;
    private Map<PExp, PExp> myLocalBindings;

    private Iterator<BindingsAndRemainingConjuncts> myOtherBindings;

    private BindingsAndRemainingConjuncts myNextReturn;

    public ConjunctGranularityBindingIterator(ImmutableConjuncts pattern,
            ImmutableConjuncts target) {

        myTarget = target;
        myTargetSize = myTarget.size();
        myLocalTargetConjunctIndex = 0;

        if (pattern.size() > 0) {
            myLocalPattern = pattern.get(0);
            myRemainingPattern = pattern.removed(0);
            myOtherBindings = DummyIterator.getInstance(myOtherBindings);
        }
        else {
            myLocalPattern = null;
            myRemainingPattern = pattern;
            myLocalBindings = new HashMap<PExp, PExp>(); //TODO: Replace with ready made component after we figure out who's changing this one
            myOtherBindings =
                    new SingletonIterator<BindingsAndRemainingConjuncts>(
                            new BindingsAndRemainingConjuncts(
                                    new HashMap<PExp, PExp>(), myTarget)); //TODO : Same as above
        }

        setUpNext();
    }

    private void setUpNext() {

        PExp curLocalTargetConjunct;
        while (!myOtherBindings.hasNext() && myLocalPattern != null
                && myLocalTargetConjunctIndex < myTargetSize) {

            curLocalTargetConjunct = myTarget.get(myLocalTargetConjunctIndex);

            try {
                myLocalBindings = myLocalPattern.bindTo(curLocalTargetConjunct);

                myOtherBindings =
                        new ConjunctGranularityBindingIterator(
                                myRemainingPattern, myTarget.removed(
                                        myLocalTargetConjunctIndex).substitute(
                                        myLocalBindings));
            }
            catch (BindingException e) {
                myOtherBindings = DummyIterator.getInstance(myOtherBindings);
            }

            myLocalTargetConjunctIndex++;
        }

        if (myOtherBindings.hasNext()) {
            BindingsAndRemainingConjuncts otherBindings =
                    myOtherBindings.next();

            otherBindings.bindings.putAll(myLocalBindings);

            myNextReturn = otherBindings;
        }
        else {
            myNextReturn = null;
        }
    }

    @Override
    public boolean hasNext() {
        return myNextReturn != null;
    }

    @Override
    public BindingsAndRemainingConjuncts next() {
        BindingsAndRemainingConjuncts retval = myNextReturn;

        setUpNext();

        return retval;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static class BindingsAndRemainingConjuncts {

        public final Map<PExp, PExp> bindings;
        public final ImmutableConjuncts remainingConjuncts;

        private BindingsAndRemainingConjuncts(Map<PExp, PExp> bindings,
                ImmutableConjuncts remainingConjuncts) {
            this.bindings = bindings;
            this.remainingConjuncts = remainingConjuncts;
        }
    }
}
