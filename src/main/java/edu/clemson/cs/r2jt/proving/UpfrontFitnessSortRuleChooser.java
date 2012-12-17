package edu.clemson.cs.r2jt.proving;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.EqualsExp;

public class UpfrontFitnessSortRuleChooser extends BlindIterativeRuleChooser {

    private final FitnessFunction<EqualsExp> myFitnessFunction;
    private final double myThreshold;

    public UpfrontFitnessSortRuleChooser(FitnessFunction<EqualsExp> fitness,
            double threshold) {
        super();
        myFitnessFunction = fitness;
        myThreshold = threshold;
    }

    @Override
    public void lock(VerificationCondition vc) {
        myLockedFlag = true;

        myFinalGlobalRules = new LinkedList<MatchReplace>();

        List<PriorityAugmentedObject<MatchReplace>> priorityList =
                new LinkedList<PriorityAugmentedObject<MatchReplace>>();

        double curFitness;
        MatchReplace curMatchReplace;
        for (EqualsExp curRule : myOriginalGlobalRules) {
            curFitness = myFitnessFunction.determineFitness(curRule, vc);

            if (curFitness < 0) {
                throw new RuntimeException("Negative fitness");
            }

            if (curFitness >= myThreshold) {
                curMatchReplace =
                        new BindReplace(curRule.getLeft(), curRule.getRight());

                priorityList.add(new PriorityAugmentedObject<MatchReplace>(
                        curMatchReplace, curFitness));
            }
        }

        Collections.sort(priorityList);

        for (PriorityAugmentedObject<MatchReplace> curRule : priorityList) {
            //System.out.println("  " + curRule.getPriority() + " \t\t " + curRule.getObject());

            myFinalGlobalRules.add(curRule.getObject());
        }
    }
}
