/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
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
        for (PExp c : model.getConsequentList()) {
            myConsequentVariableNames.addAll(c.getSymbolNames());
        }
    }

    public double calculateFitness(Transformation t) {
        Set<String> introduced =
                new HashSet<String>(t.getReplacementSymbolNames());
        introduced.removeAll(myConsequentVariableNames);

        double simplificationFactor =
                Math.pow(0.8, t.functionApplicationCountDelta());

        return Math.min(
                Math.pow(0.5, introduced.size()) * simplificationFactor, 1.0);
    }
}
