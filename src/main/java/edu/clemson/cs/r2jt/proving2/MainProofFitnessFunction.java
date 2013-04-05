/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class MainProofFitnessFunction {

    private Set<String> myConsequentVariableNames = new HashSet<String>();

    public MainProofFitnessFunction(PerVCProverModel model) {
        for (Conjunct c : model.getConsequentList()) {
            myConsequentVariableNames
                    .addAll(c.getExpression().getSymbolNames());
        }
    }

    public double calculateFitness(Transformation t) {
        double result = 0;

        if (t instanceof SubstituteInPlaceInConsequent) {
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

        if (result == 0) {
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
