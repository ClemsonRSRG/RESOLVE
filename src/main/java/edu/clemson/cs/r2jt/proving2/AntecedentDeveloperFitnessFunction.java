/**
 * AntecedentDeveloperFitnessFunction.java
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
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.proving2.transformations.StrengthenConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class AntecedentDeveloperFitnessFunction
        implements
            FitnessFunction<Transformation> {

    private Set<String> myConsequentVariableNames = new HashSet<String>();

    public AntecedentDeveloperFitnessFunction(PerVCProverModel model) {
        for (Conjunct c : model.getConsequentList()) {
            myConsequentVariableNames
                    .addAll(c.getExpression().getSymbolNames());
        }
    }

    @Override
    public double calculateFitness(Transformation t) {
        double result = 0;

        if (t.couldAffectConsequent() || t.introducesQuantifiedVariables()) {
            result = -1;
        }
        else if (AutomatedProver.H_DETECT_IDENTITY_EXPANSION
                && t instanceof ExpandAntecedentBySubstitution) {
            ExpandAntecedentBySubstitution tAsSIPIC =
                    (ExpandAntecedentBySubstitution) t;

            PExp pattern = tAsSIPIC.getMatchPattern();
            PExp replacement = tAsSIPIC.getTransformationTemplate();
            if (pattern.getFunctionApplications().isEmpty()
                    && pattern.getQuantifiedVariables().size() == 1
                    && replacement.getQuantifiedVariables().contains(
                            pattern.getQuantifiedVariables().iterator().next())) {
                result = -1;
            }
        }

        return result;
    }

    private double unitAtan(int i) {
        return (Math.atan(i) * 2 / Math.PI + 1) / 2;
    }
}
