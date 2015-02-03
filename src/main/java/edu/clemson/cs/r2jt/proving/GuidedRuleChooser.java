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
package edu.clemson.cs.r2jt.proving;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;

public class GuidedRuleChooser extends RuleProvider {

    private List<MatchReplace> myGlobalRules = new LinkedList<MatchReplace>();
    private List<Exp> myExpCorrespondance = new LinkedList<Exp>();

    private boolean myLockedFlag;

    private DirectReplaceWrapper myAntecedentWrapper =
            new DirectReplaceWrapper();

    public GuidedRuleChooser() {
        myLockedFlag = false;
    }

    public void addRules(List<String> names, List<Exp> rules) {
        Iterator<String> namesIterator = names.iterator();
        Iterator<Exp> rulesIterator = rules.iterator();

        while (namesIterator.hasNext()) {
            addRule(namesIterator.next(), rulesIterator.next());
        }
    }

    public void addRule(String friendlyName, Exp rule) {
        if (myLockedFlag) {
            throw new IllegalStateException();
        }

        if (rule instanceof EqualsExp) {
            EqualsExp equivalency = (EqualsExp) rule;

            if (equivalency.getOperator() == EqualsExp.EQUAL) {
                //Substitute right expression for left
                MatchReplace matcher =
                        new BindReplace(equivalency.getLeft(), equivalency
                                .getRight());
                myGlobalRules.add(matcher);
                myExpCorrespondance.add(rule);

                //Substitute left expression for left
                matcher =
                        new BindReplace(equivalency.getRight(), equivalency
                                .getLeft());
                myGlobalRules.add(matcher);
                myExpCorrespondance.add(rule);
            }
        }
        else {
            System.out.println("BlindIterativeRule.addRule --- "
                    + "Non equals Theorem.");
            System.out.println(rule.toString(0));
        }
    }

    public int getRuleCount() {
        return myGlobalRules.size();
    }

    public KnownSizeIterator<MatchReplace> consider(VerificationCondition vC,
            int curLength, Metrics metrics,
            Deque<VerificationCondition> pastStates) {

        //We only want those antecedents that are in the form of an equality,
        //and for each of those we need it going both left-to-right and 
        //right-to-left
        List<Exp> antecedentTransforms =
                buildFinalAntecedentList(vC.getAntecedents());

        Iterator<MatchReplace> antecedentIterator =
                new LazyActionIterator<Exp, MatchReplace>(antecedentTransforms
                        .iterator(), myAntecedentWrapper);

        ChainingIterator<MatchReplace> totalIterator =
                new ChainingIterator<MatchReplace>(antecedentIterator,
                        myGlobalRules.iterator());

        GuidedListSelectIterator<MatchReplace> finalIterator =
                new GuidedListSelectIterator<MatchReplace>("VC " + vC.getName()
                        + " - Select a proof rule...", vC.getAntecedents()
                        .toString()
                        + " =====> " + vC.getConsequents().toString(),
                        totalIterator);

        return new SizedIterator<MatchReplace>(finalIterator,
                antecedentTransforms.size() + myGlobalRules.size());
    }

    private List<Exp> buildFinalAntecedentList(List<Exp> originalAntecedents) {
        List<Exp> antecedentTransforms = new LinkedList<Exp>();
        for (Exp antecedent : originalAntecedents) {
            if (antecedent instanceof EqualsExp) {
                EqualsExp antecedentAsEqualsExp = (EqualsExp) antecedent;

                if (antecedentAsEqualsExp.getOperator() == EqualsExp.EQUAL) {

                    antecedentTransforms.add(antecedent);

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

    public boolean isLocked() {
        return myLockedFlag;
    }

    public void removeRule(Exp exp) {
        if (myLockedFlag) {
            throw new IllegalStateException();
        }

        Iterator correspondance = myExpCorrespondance.iterator();
        Iterator rules = myGlobalRules.iterator();

        while (correspondance.hasNext()) {
            if (correspondance.next() == exp) {
                correspondance.remove();
                rules.remove();
            }
        }
    }

    public void setLocked(Boolean locked) {
        myLockedFlag = locked;
    }

    private class DirectReplaceWrapper
            implements
                Transformer<Exp, MatchReplace> {

        public DirectReplace transform(Exp source) {
            if (!(source instanceof EqualsExp)) {
                equalsOnlyException(source);
            }

            EqualsExp sourceAsEqualsExp = (EqualsExp) source;
            return new DirectReplace(sourceAsEqualsExp.getLeft(),
                    sourceAsEqualsExp.getRight());
        }
    }

    private void equalsOnlyException(Exp e) {
        throw new RuntimeException("The prover does not yet work for "
                + "theorems not in the form of an equality, such as:\n"
                + e.toString(0));
    }

    public int getApproximateRuleSetSize() {
        return -1;
    }
}
