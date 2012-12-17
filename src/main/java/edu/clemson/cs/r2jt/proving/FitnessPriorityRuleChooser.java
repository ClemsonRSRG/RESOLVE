package edu.clemson.cs.r2jt.proving;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;

public class FitnessPriorityRuleChooser /* XXX : implements RuleProvider */{

    protected List<MatchReplaceWithOriginalExp> myGlobalRules =
            new LinkedList<MatchReplaceWithOriginalExp>();

    protected List<EqualsExp> myExpCorrespondance = new LinkedList<EqualsExp>();

    protected boolean myLockedFlag;

    private final FitnessFunction<EqualsExp> myFitnessFunction;
    private final double myThreshold;

    protected DirectReplaceWrapper myAntecedentWrapper =
            new DirectReplaceWrapper();

    public FitnessPriorityRuleChooser(FitnessFunction<EqualsExp> fitness,
            double threshold) {
        myFitnessFunction = fitness;
        myThreshold = threshold;
    }

    /**
     * <p>Adds each <code>Exp</code> from <code>rules</code> to the chooser with
     * the name in the corresponding position in <code>names</code> 
     * individually, as though by multiple calls to <code>addRule()</code>.</p>
     * 
     * @param names A list of friendly theorem names, corresponding to
     *              the elements of <code>rules</code>.
     * @param rules The list of theorems.
     * 
     * @throws NoSuchElementException If <code>names.size() != 
     * 		rules.size()</code>.
     */
    public void addRules(List<String> names, List<Exp> rules) {
        Iterator<String> namesIterator = names.iterator();
        Iterator<Exp> rulesIterator = rules.iterator();

        while (namesIterator.hasNext()) {
            addRule(namesIterator.next(), rulesIterator.next());
        }
    }

    /**
     * <p>Takes an <code>Exp</code> that is in the form of an equality and adds
     * two new substitutions to this chooser: one substituting the expression
     * on the left of the equals with the one on the right, and the other
     * substituting the expression on the right with the left.</p>
     * 
     * <p>The chooser must not be locked or an exception will be thrown.</p>
     * 
     * @param friendlyName A friendly, human readable name for the rule.
     * @param rule The rule itself.
     * 
     * @throws IllegalStateException If the chooser is locked.
     * @throws NullPointerException If <code>friendlyName</code> or 
     * 		<code>rule</code> is <code>null</code>.
     */
    public void addRule(String friendlyName, Exp rule) {
        if (myLockedFlag) {
            throw new IllegalStateException();
        }

        if (rule instanceof EqualsExp) {
            EqualsExp equivalency = (EqualsExp) rule;

            MatchReplaceWithOriginalExp record;
            if (equivalency.getOperator() == EqualsExp.EQUAL) {
                //Substitute left expression for right
                record = new MatchReplaceWithOriginalExp();
                record.matchReplace =
                        new BindReplace(equivalency.getLeft(), equivalency
                                .getRight());
                record.originalExp = equivalency;
                myGlobalRules.add(record);
                myExpCorrespondance.add(equivalency);

                //Substitute right expression for left
                EqualsExp inverseEquivalency =
                        (EqualsExp) Exp.copy(equivalency);
                inverseEquivalency.setLeft(equivalency.getRight());
                inverseEquivalency.setRight(equivalency.getLeft());

                record = new MatchReplaceWithOriginalExp();
                record.matchReplace =
                        new BindReplace(equivalency.getRight(), equivalency
                                .getLeft());
                record.originalExp = inverseEquivalency;
                myGlobalRules.add(record);
                myExpCorrespondance.add(equivalency);
            }
        }
        else {
            System.out.println("Prover.BlindIterativeRuleChooser.addRule --- "
                    + "Non equals Theorem.");
            System.out.println(rule.toString(0));
        }
    }

    /**
     * <p>Returns the number of rules currently in this chooser.</p>
     * 
     * @return The number of rules.
     */
    public int getRuleCount() {
        return myGlobalRules.size();
    }

    public KnownSizeIterator<MatchReplace> consider(VerificationCondition vC,
            int curLength, Metrics metrics,
            Deque<VerificationCondition> pastStates) {

        //We only want those antecedents that are in the form of an 
        //equality, and for each of those we need it going both 
        //left-to-right and right-to-left
        List<EqualsExp> antecedentTransforms =
                buildFinalAntecedentList(vC.getAntecedents());

        List<EqualsExp> prioritizedAntecedents =
                prioritizeAndFilter(antecedentTransforms, vC);

        Iterator<MatchReplace> antecedentIterator =
                new LazyActionIterator<EqualsExp, MatchReplace>(
                        antecedentTransforms.iterator(), myAntecedentWrapper);

        /*//XXX : This just commented out to get rid of error for running purposes
        
        ChainingIterator<MatchReplace> finalIterator =
        	new ChainingIterator<MatchReplace>(antecedentIterator, 
        			myGlobalRules.iterator());
        
        
        return new SizedIterator<MatchReplace>(finalIterator, 
        		antecedentTransforms.size() + myGlobalRules.size());
         */
        return null;
    }

    protected List<EqualsExp> buildFinalAntecedentList(
            List<Exp> originalAntecedents) {
        List<EqualsExp> antecedentTransforms = new LinkedList<EqualsExp>();
        for (Exp antecedent : originalAntecedents) {
            if (antecedent instanceof EqualsExp) {
                EqualsExp antecedentAsEqualsExp = (EqualsExp) antecedent;

                if (antecedentAsEqualsExp.getOperator() == EqualsExp.EQUAL) {
                    antecedentTransforms.add(antecedentAsEqualsExp);

                    EqualsExp flippedAntecedent =
                            new EqualsExp(antecedentAsEqualsExp.getLocation(),
                                    antecedentAsEqualsExp.getRight(),
                                    antecedentAsEqualsExp.getOperator(),
                                    antecedentAsEqualsExp.getLeft());
                    antecedentTransforms.add(flippedAntecedent);
                }
            }
        }

        return antecedentTransforms;
    }

    /**
     * <p>Returns whether or not this chooser is locked.</p>
     * 
     * @return Whether or not this chooser is locked.
     */
    public boolean isLocked() {
        return myLockedFlag;
    }

    /**
     * <p>Removes any rules that were added as a result of adding the given
     * <code>Exp</code>.  If there is no such rule, takes no action.</p>
     * 
     * <p>The chooser must not be locked or an exception will be thrown.</p>
     * 
     * @param exp The rule that should be removed from the chooser.
     * 
     * @throws IllegalStateException If the chooser is locked.
     * @throws NullPointerException If <code>friendlyName</code> is 
     * 		<code>null</code>.
     */
    public void removeRule(Exp exp) {
        if (myLockedFlag) {
            throw new IllegalStateException();
        }

        Iterator correspondance = myExpCorrespondance.iterator();
        Iterator rules = myGlobalRules.iterator();

        while (correspondance.hasNext()) {
            rules.next();
            if (correspondance.next() == exp) {
                correspondance.remove();
                rules.remove();
            }
        }
    }

    private List<EqualsExp> prioritizeAndFilter(List<EqualsExp> original,
            VerificationCondition vc) {

        List<PriorityAugmentedObject<EqualsExp>> originalWithPriorities =
                new LinkedList<PriorityAugmentedObject<EqualsExp>>();

        double curFitness;

        for (EqualsExp curEqualsExp : original) {
            curFitness = myFitnessFunction.determineFitness(curEqualsExp, vc);

            if (curFitness >= myThreshold) {
                originalWithPriorities
                        .add(new PriorityAugmentedObject<EqualsExp>(
                                curEqualsExp, curFitness));
            }
        }

        Collections.sort(originalWithPriorities);

        List<EqualsExp> finalList = new LinkedList<EqualsExp>();

        for (PriorityAugmentedObject<EqualsExp> curAugmentedExp : originalWithPriorities) {

            finalList.add(curAugmentedExp.getObject());
        }

        return finalList;
    }

    private List<MatchReplace> prioritizeAndFilterGlobalRules(
            VerificationCondition vc) {

        List<PriorityAugmentedObject<EqualsExp>> originalWithPriorities =
                new LinkedList<PriorityAugmentedObject<EqualsExp>>();

        double curFitness;

        EqualsExp curEqualsExp;
        MatchReplace curMatchReplace;
        for (MatchReplaceWithOriginalExp curRule : myGlobalRules) {
            curEqualsExp = curRule.originalExp;
            curMatchReplace = curRule.matchReplace;

            curFitness = myFitnessFunction.determineFitness(curEqualsExp, vc);

            if (curFitness >= myThreshold) {
                originalWithPriorities
                        .add(new PriorityAugmentedObject<EqualsExp>(
                                curEqualsExp, curFitness));
            }
        }

        Collections.sort(originalWithPriorities);

        List<EqualsExp> finalList = new LinkedList<EqualsExp>();

        for (PriorityAugmentedObject<EqualsExp> curAugmentedExp : originalWithPriorities) {

            finalList.add(curAugmentedExp.getObject());
        }

        /* XXX : return finalList; */
        return null;
    }

    private class DirectReplaceWrapper
            implements
                Transformer<EqualsExp, MatchReplace> {

        public DirectReplace transform(EqualsExp source) {
            return new DirectReplace(source.getLeft(), source.getRight());
        }
    }

    private class MatchReplaceWithOriginalExp {

        public MatchReplace matchReplace;
        public EqualsExp originalExp;
    }
}
