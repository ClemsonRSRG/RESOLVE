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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;

/**
 * <p>A <code>ConsequentWeakeningTransformer</code> transforms a given
 * <code>Consequent</code> by applying a one-way replacement that possibly
 * weakens (but definitely does not strengthen) the assertion of the consequent.
 * That is, the transformation applies an implication theorem in reverse, 
 * finding conjuncts of the given consequent that match the consequent of the 
 * theorem and replacing them with the antecedent of the theorem--since if we 
 * can establish the antecedent, we can definitely establish the consequent.</p>
 */
public class ConsequentConjunctWeakeningTransformer
        implements
            ConsequentTransformer {

    private final Antecedent myTheoremAntecedent;
    private final Consequent myTheoremConsequent;

    public ConsequentConjunctWeakeningTransformer(Antecedent theoremAntecedent,
            Consequent theoremConsequent) {

        myTheoremAntecedent = theoremAntecedent;
        myTheoremConsequent = theoremConsequent;
    }

    public Antecedent getTheoremAntecedent() {
        return myTheoremAntecedent;
    }

    public Consequent getTheoremConsequent() {
        return myTheoremConsequent;
    }

    @Override
    public Iterator<Consequent> transform(Consequent source) {
        return new BindingApplyer(new ConjunctGranularityBindingIterator(
                myTheoremConsequent, source));
    }

    private class BindingApplyer implements Iterator<Consequent> {

        private final ConjunctGranularityBindingIterator myBindings;

        public BindingApplyer(ConjunctGranularityBindingIterator i) {
            myBindings = i;
        }

        @Override
        public boolean hasNext() {
            return myBindings.hasNext();
        }

        @Override
        public Consequent next() {
            ConjunctGranularityBindingIterator.BindingsAndRemainingConjuncts binding =
                    myBindings.next();

            //We need to make sure that the antecedent doesn't introduce
            //quantified variable names that already exist
            Iterable<PSymbol> unboundVariables =
                    getUnboundQuantifiedVariables(myTheoremAntecedent,
                            binding.bindings);
            Map<PExp, PExp> renamings =
                    renameAsNecessary(unboundVariables,
                            binding.remainingConjuncts);
            Antecedent newAntecedent =
                    myTheoremAntecedent.substitute(renamings);
            newAntecedent = newAntecedent.substitute(binding.bindings);

            Consequent newConsequent =
                    new Consequent(binding.remainingConjuncts
                            .appended(newAntecedent.instantiate()));

            return newConsequent;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private static Map<PExp, PExp> renameAsNecessary(
            Iterable<PSymbol> originals, ImmutableConjuncts destination) {

        Map<PExp, PExp> accumulator = new HashMap<PExp, PExp>();

        for (PSymbol e : originals) {
            renameAsNecessary(e, destination, accumulator);
        }

        return accumulator;
    }

    private static void renameAsNecessary(PSymbol original,
            ImmutableConjuncts destination, Map<PExp, PExp> accumulator) {

        final String originalName = original.name;
        String consideredName = originalName;
        while (containsName(consideredName, destination)) {
            consideredName = "?" + consideredName;
        }

        PSymbol newExp;

        if (consideredName == originalName) {
            newExp = original;
        }
        else {
            newExp = original.setName(consideredName);
        }

        accumulator.put(original, newExp);
    }

    private static boolean containsName(String consideredName,
            ImmutableConjuncts destination) {

        boolean retval = false;
        Iterator<PExp> destinations = destination.iterator();
        while (!retval && destinations.hasNext()) {
            retval = destinations.next().containsName(consideredName);
        }

        return retval;
    }

    private static Iterable<PSymbol> getUnboundQuantifiedVariables(
            ImmutableConjuncts source, Map<PExp, PExp> bindings) {

        List<PSymbol> finalVariables = new LinkedList<PSymbol>();

        Iterable<PSymbol> quantifiedVariables = source.getQuantifiedVariables();

        for (PSymbol var : quantifiedVariables) {
            if (!containsEquivalentKey(var, bindings)) {
                finalVariables.add(var);
            }
        }

        return finalVariables;
    }

    private static boolean containsEquivalentKey(PExp query, Map<PExp, PExp> map) {
        boolean retval = false;

        Iterator<PExp> keys = map.keySet().iterator();
        while (!retval && keys.hasNext()) {
            retval = keys.next().equals(query);
        }

        return retval;
    }

    public String toString() {
        return ("Strengthen consequent: " + myTheoremConsequent + " to " + myTheoremAntecedent)
                .replace('\n', ' ');
    }
}
