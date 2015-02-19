/**
 * MainProofFitnessFunction.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.transformations.StrengthenConsequent;
import edu.clemson.cs.r2jt.rewriteprover.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
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
