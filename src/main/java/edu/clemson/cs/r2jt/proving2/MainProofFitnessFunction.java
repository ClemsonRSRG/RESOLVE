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
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.StrengthenConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class MainProofFitnessFunction
        implements
            FitnessFunction<Transformation> {

    private Set<String> myConsequentVariableNames = new HashSet<String>();

    public MainProofFitnessFunction(PerVCProverModel model) {
        for (Conjunct c : model.getConsequentList()) {
            myConsequentVariableNames
                    .addAll(c.getExpression().getSymbolNames());
        }
    }

    @Override
    public double calculateFitness(Transformation t) {
        double result = 0;

        if (t.couldAffectAntecedent()
                || (!(t instanceof StrengthenConsequent) && t
                        .introducesQuantifiedVariables())) {
            result = -1;
        }
        else if (AutomatedProver.H_DETECT_IDENTITY_EXPANSION
                && t instanceof SubstituteInPlaceInConsequent) {
            SubstituteInPlaceInConsequent tAsSIPIC =
                    (SubstituteInPlaceInConsequent) t;

            PExp pattern = tAsSIPIC.getPattern();
            PExp replacement = tAsSIPIC.getReplacement();
            if (pattern.getFunctionApplications().isEmpty()
                    && pattern.getQuantifiedVariables().size() == 1
                    && replacement.getQuantifiedVariables().contains(
                            pattern.getQuantifiedVariables().iterator().next())) {
                result = -1;
            }
        }

        if (result == 0 && AutomatedProver.H_BEST_FIRST_CONSEQUENT_EXPLORATION) {
            Set<String> introduced =
                    new HashSet<String>(t.getReplacementSymbolNames());
            introduced.removeAll(myConsequentVariableNames);

            double simplificationFactor =
                    unitAtan(t.functionApplicationCountDelta() * -1);

            result =
                    Math.min(Math.pow(0.5, introduced.size())
                            * simplificationFactor, 1.0);
        }

        return result;
    }

    private double unitAtan(int i) {
        return (Math.atan(i) * 2 / Math.PI + 1) / 2;
    }
}
