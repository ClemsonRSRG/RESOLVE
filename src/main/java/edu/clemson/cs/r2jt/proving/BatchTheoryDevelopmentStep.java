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

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>BatchTheoryDevelopmentStep</code> extends the antecedents of given
 * VCs by repeatedly applying a set of implication theorems in a finite number
 * of rounds, where each round is a complete pass through all the theorems.</p>
 */
public class BatchTheoryDevelopmentStep implements VCTransformer {

    private final Iterable<PExp> myGlobalTheorems;
    private final int myIterationCount;
    private DevelopmentAlternativesTransformer myExtenders =
            new DevelopmentAlternativesTransformer();

    public BatchTheoryDevelopmentStep(Iterable<PExp> globalTheorems,
            int iterationCount) {
        myGlobalTheorems = globalTheorems;
        myIterationCount = iterationCount;
    }

    public void addImplicationTheorem(Antecedent a, Consequent c) {
        ConditionalAntecedentExtender e =
                new ConditionalAntecedentExtender(a, c, myGlobalTheorems);

        addExtender(e);
    }

    public void addExtender(ConditionalAntecedentExtender e) {
        myExtenders.addAlternative(new NewTermsOnlyDeveloper(e));
    }

    @Override
    public Iterator<VC> transform(VC vc) {

        AccumulatingAntecedentExtender accumulator =
                new AccumulatingAntecedentExtender(myExtenders);

        RepeatedApplicationTransformer<Antecedent> repeater =
                new RepeatedApplicationTransformer<Antecedent>(
                        new AntecedentSimplifier(new DevelopmentAppender(
                                accumulator)), myIterationCount);

        return new StaticConsequentIterator(vc.getSourceName(), repeater
                .transform(vc.getAntecedent()), vc.getConsequent());
    }

    @Override
    public String toString() {
        return "general theory development step";
    }

    @Override
    public Antecedent getPattern() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public Consequent getReplacementTemplate() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return true;
    }
}
