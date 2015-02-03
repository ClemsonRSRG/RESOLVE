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

import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>An <code>ExistentialInstantiationStep</code> attempts to bind known facts
 * against existentially quantified variables in the conjunct of provided VCs.
 * </p>
 */
public class ExistentialInstantiationStep implements VCTransformer {

    private static final Iterator<VC> DUMMY_ITERATOR =
            DummyIterator.getInstance((Iterator<VC>) null);
    private final Iterable<PExp> myGlobalFacts;

    public ExistentialInstantiationStep(Iterable<PExp> globalFact) {

        myGlobalFacts = globalFact;
    }

    @Override
    public Iterator<VC> transform(VC original) {
        Iterator<VC> soFar = DummyIterator.getInstance(DUMMY_ITERATOR);

        Antecedent originalAntecedent = original.getAntecedent();
        Consequent originalConsequent = original.getConsequent();

        int consequentIndex = 0;
        for (PExp e : original.getConsequent()) {

            if (e.containsExistential()) {
                soFar =
                        new ChainingIterator<VC>(
                                soFar,
                                new StaticAntecedentIterator(
                                        original.getSourceName(),
                                        originalAntecedent,
                                        new SingleExistentialInstantiator(
                                                e,
                                                originalAntecedent,
                                                originalConsequent
                                                        .removed(consequentIndex))));
            }

            consequentIndex++;
        }

        return soFar;
    }

    @Override
    public Antecedent getPattern() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public Consequent getReplacementTemplate() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    private class SingleExistentialInstantiator implements Iterator<Consequent> {

        private final PExp myExistential;
        private final Consequent myOriginal;
        private final Iterator<PExp> myFactIterator;
        private Consequent myNextConsequent;

        public SingleExistentialInstantiator(PExp existentialExpression,
                Antecedent vcAntecedent, Consequent remainingConsequent) {
            myExistential = existentialExpression;
            myOriginal = remainingConsequent;
            myFactIterator =
                    new ChainingIterator<PExp>(vcAntecedent.iterator(),
                            myGlobalFacts.iterator());

            setUpNext();
        }

        private void setUpNext() {

            PExp curFact;
            Map<PExp, PExp> binding = null;
            while (myFactIterator.hasNext() && binding == null) {
                curFact = myFactIterator.next();

                try {
                    binding = myExistential.bindTo(curFact);
                }
                catch (BindingException e) {}
            }

            if (binding != null) {
                myNextConsequent = myOriginal.substitute(binding);
            }
            else {
                myNextConsequent = null;
            }
        }

        @Override
        public boolean hasNext() {
            return myNextConsequent != null;
        }

        @Override
        public Consequent next() {
            Consequent retval = myNextConsequent;

            setUpNext();

            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return "Bind Existential";
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return false;
    }
}
