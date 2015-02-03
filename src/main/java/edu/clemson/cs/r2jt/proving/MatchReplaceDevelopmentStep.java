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

public class MatchReplaceDevelopmentStep implements VCTransformer {

    private final AntecedentTransformer myTransformer;
    private final Antecedent myTheoremAntecedent;
    private final Consequent myTheoremConsequent;

    private final boolean myIntroducesQuantifiedVariablesFlag;

    public MatchReplaceDevelopmentStep(NewMatchReplace m) {
        myTransformer =
                new AntecedentTransformerAdapter(
                        new ApplicatorConjunctsTransformer(
                                new ExtendingApplicatorFactory(m)));

        myTheoremAntecedent = new Antecedent(m.getPattern());
        myTheoremConsequent = new Consequent(m.getExpansionTemplate());

        myIntroducesQuantifiedVariablesFlag =
                myTheoremConsequent
                        .containsQuantifiedVariableNotIn(myTheoremAntecedent);
    }

    @Override
    public Iterator<VC> transform(VC original) {
        return new StaticConsequentIterator(original.getSourceName(),
                myTransformer.transform(original.getAntecedent()), original
                        .getConsequent());
    }

    @Override
    public String toString() {
        return myTransformer.toString();
    }

    @Override
    public Antecedent getPattern() {
        return myTheoremAntecedent;
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myTheoremConsequent;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return myIntroducesQuantifiedVariablesFlag;
    }
}
