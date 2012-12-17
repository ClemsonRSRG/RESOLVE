package edu.clemson.cs.r2jt.proving;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * XXX : This class should be modified so that it and FitnessPriorityRuleChooser
 *       can be somewhat unified by having this one be the superclass of the
 *       other
 */
public class BlindIterativeRuleChooser extends RuleProvider {

    protected List<MatchReplace> myFinalGlobalRules;

    protected List<EqualsExp> myOriginalGlobalRules =
            new LinkedList<EqualsExp>();

    protected List<EqualsExp> myExpCorrespondance = new LinkedList<EqualsExp>();

    protected boolean myLockedFlag;

    protected DirectReplaceWrapper myAntecedentWrapper =
            new DirectReplaceWrapper();

    public BlindIterativeRuleChooser() {
        myLockedFlag = false;
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

            if (equivalency.getOperator() == EqualsExp.EQUAL) {
                //Substitute left expression for right
                myOriginalGlobalRules.add(equivalency);
                myExpCorrespondance.add(equivalency);

                //Substitute right expression for left
                EqualsExp inverseEquivalency = new EqualsExp();
                inverseEquivalency.setOperator(EqualsExp.EQUAL);

                //EqualsExp inverseEquivalency = (EqualsExp) equivalency.copy();
                inverseEquivalency.setLeft(equivalency.getRight());
                inverseEquivalency.setRight(equivalency.getLeft());
                myOriginalGlobalRules.add(inverseEquivalency);
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
        return myOriginalGlobalRules.size();
    }

    public KnownSizeIterator<MatchReplace> consider(VerificationCondition vC,
            int curLength, Metrics metrics,
            Deque<VerificationCondition> pastStates) {

        //We only want those antecedents that are in the form of an 
        //equality, and for each of those we need it going both 
        //left-to-right and right-to-left
        List<EqualsExp> antecedentTransforms =
                buildFinalAntecedentList(vC.getAntecedents());

        Iterator<MatchReplace> antecedentIterator =
                new LazyActionIterator<EqualsExp, MatchReplace>(
                        antecedentTransforms.iterator(), myAntecedentWrapper);

        ChainingIterator<MatchReplace> finalIterator =
                new ChainingIterator<MatchReplace>(antecedentIterator,
                        myFinalGlobalRules.iterator());

        return new SizedIterator<MatchReplace>(finalIterator,
                antecedentTransforms.size() + myFinalGlobalRules.size());
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
        Iterator rules = myOriginalGlobalRules.iterator();

        while (correspondance.hasNext()) {
            rules.next();
            if (correspondance.next() == exp) {
                correspondance.remove();
                rules.remove();
            }
        }
    }

    public void lock(VerificationCondition vc) {
        myLockedFlag = true;

        myFinalGlobalRules = new LinkedList<MatchReplace>();
        MatchReplace matcher;
        for (EqualsExp e : myOriginalGlobalRules) {
            matcher = new BindReplace(e.getLeft(), e.getRight());
            myFinalGlobalRules.add(matcher);
        }
    }

    public void unlock() {
        myLockedFlag = false;
    }

    private class DirectReplaceWrapper
            implements
                Transformer<EqualsExp, MatchReplace> {

        public DirectReplace transform(EqualsExp source) {
            return new DirectReplace(source.getLeft(), source.getRight());
        }
    }

    private void equalsOnlyException(Exp e) {
        throw new RuntimeException("The prover does not yet work for "
                + "theorems not in the form of an equality, such as:\n"
                + e.toString(0));
    }

    public int getApproximateRuleSetSize() {
        return myOriginalGlobalRules.size();
    }
}
