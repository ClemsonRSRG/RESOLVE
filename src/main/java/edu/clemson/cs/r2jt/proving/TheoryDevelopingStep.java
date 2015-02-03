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

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>TheoryDevelopingStep</code> uses an implication theorem to expand
 * the set of known facts in the VC's antecedent, by applying all possible
 * bindings of the theorem's antecedent against the VC's antecedent and globally
 * known facts, then extending the VC antecedent with the consequent of the 
 * theorem under each of those bindings.</p>
 * 
 * <p>Note that the random quirk mentioned in 
 * <code>ConditionalAntecedentExtender</code>'s class comments applies here as
 * well.</p>
 */
public class TheoryDevelopingStep implements VCTransformer {

    private final AntecedentDeveloper myDerivedTransformer;
    private final Antecedent myAntecedent;
    private final Consequent myConsequent;

    private final boolean myIntroducesQuantifiedVariablesFlag;

    public TheoryDevelopingStep(Antecedent theoremAntecedent,
            Consequent theoremConsequent, Iterable<PExp> globalFacts) {

        myDerivedTransformer =
                new AccumulatingAntecedentExtender(
                        new ConditionalAntecedentExtender(theoremAntecedent,
                                theoremConsequent, globalFacts));

        myAntecedent = theoremAntecedent;
        myConsequent = theoremConsequent;

        myIntroducesQuantifiedVariablesFlag =
                myConsequent.containsQuantifiedVariableNotIn(myAntecedent);
    }

    @Override
    public Iterator<VC> transform(VC original) {

        Antecedent originalAntecedent = original.getAntecedent();

        return new StaticConsequentIterator(original.getSourceName(),
                new AntecedentDevelopmentIterator(originalAntecedent,
                        myDerivedTransformer.transform(originalAntecedent)),
                original.getConsequent());
    }

    @Override
    public String toString() {
        return "Develop antecedent with " + myDerivedTransformer;
    }

    @Override
    public Antecedent getPattern() {
        return myAntecedent;
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myConsequent;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return myIntroducesQuantifiedVariablesFlag;
    }
}
