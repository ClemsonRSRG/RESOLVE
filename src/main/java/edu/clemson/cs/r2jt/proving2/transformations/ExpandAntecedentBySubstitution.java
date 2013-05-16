/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.applications.GeneralApplication;
import edu.clemson.cs.r2jt.proving2.model.AtLeastOneLocalTheoremBinder;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.AbstractBinder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.utilities.InductiveSiteIteratorIterator;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ExpandAntecedentBySubstitution implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private final PExp myMatchPattern;
    private final List<PExp> myMatchPatternConjuncts;
    private final int myMatchPatternConjunctsSize;

    private final PExp myTransformationTemplate;
    private final List<PExp> myTransformationTemplateConjuncts;

    private final Theorem myTheorem;

    public ExpandAntecedentBySubstitution(Theorem t, PExp tMatchPattern,
            PExp tTransformationTemplate) {
        myMatchPattern = tMatchPattern;
        myMatchPatternConjuncts = tMatchPattern.splitIntoConjuncts();
        myMatchPatternConjunctsSize = myMatchPatternConjuncts.size();

        myTransformationTemplate = tTransformationTemplate;
        myTransformationTemplateConjuncts =
                tTransformationTemplate.splitIntoConjuncts();

        myTheorem = t;
    }

    public Theorem getTheorem() {
        return myTheorem;
    }

    public PExp getMatchPattern() {
        return myMatchPattern;
    }

    public PExp getTransformationTemplate() {
        return myTransformationTemplate;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<Application> result;

        Iterator<BindResult> bindResults =
                m
                        .bind(Collections
                                .singleton((Binder) new SkipOneTopLevelAntecedentBinder(
                                        myMatchPattern)));

        result =
                new LazyMappingIterator<BindResult, Application>(bindResults,
                        BIND_RESULT_TO_APPLICATION);

        //We might also have to account for the situation where the conjuncts of
        //the match span multiple theorems
        if (myMatchPatternConjuncts.size() > 1) {
            Set<Binder> binders = new HashSet<Binder>();

            Binder binder;
            for (PExp matchConjunct : myMatchPatternConjuncts) {
                binder =
                        new AtLeastOneLocalTheoremBinder(matchConjunct,
                                myMatchPatternConjunctsSize);

                binders.add(binder);
            }

            result =
                    new ChainingIterator(result,
                            new LazyMappingIterator<BindResult, Application>(m
                                    .bind(binders), BIND_RESULT_TO_APPLICATION));
        }

        return result;
    }

    @Override
    public boolean couldAffectAntecedent() {
        return true;
    }

    @Override
    public boolean couldAffectConsequent() {
        return false;
    }

    @Override
    public int functionApplicationCountDelta() {
        return myTransformationTemplate.getFunctionApplications().size()
                - myMatchPattern.getFunctionApplications().size();
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        Set<PSymbol> introduced =
                new HashSet<PSymbol>(myTransformationTemplate
                        .getQuantifiedVariables());
        introduced.removeAll(myMatchPattern.getQuantifiedVariables());

        return !introduced.isEmpty();
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        return myMatchPattern.getSymbolNames();
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return myTransformationTemplate.getSymbolNames();
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.EQUIVALENT;
    }

    private class SkipOneTopLevelAntecedentBinder extends AbstractBinder {

        public SkipOneTopLevelAntecedentBinder(PExp pattern) {
            super(pattern);
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return new InductiveSiteIteratorIterator(
                    new SkipOneTopLevelAntecedentIterator(m
                            .topLevelAntecedentSiteIterator(), myTheorem));
        }
    }

    private static class SkipOneTopLevelAntecedentIterator
            implements
                Iterator<Site> {

        private final Conjunct myTheoremToSkip;
        private final Iterator<Site> myBaseIterator;
        private Site myNextReturn;

        public SkipOneTopLevelAntecedentIterator(Iterator<Site> sites,
                Conjunct theoremToSkip) {
            myTheoremToSkip = theoremToSkip;
            myBaseIterator = sites;

            setUpNext();
        }

        private void setUpNext() {
            if (myBaseIterator.hasNext()) {
                myNextReturn = myBaseIterator.next();

                if (myNextReturn.conjunct == myTheoremToSkip) {
                    setUpNext();
                }
            }
            else {
                myNextReturn = null;
            }
        }

        @Override
        public boolean hasNext() {
            return (myNextReturn != null);
        }

        @Override
        public Site next() {
            if (myNextReturn == null) {
                throw new UnsupportedOperationException();
            }

            Site result = myNextReturn;

            setUpNext();

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class BindResultToApplication
            implements
                Mapping<BindResult, Application> {

        @Override
        public Application map(BindResult input) {
            Map<Conjunct, PExp> newValues = new HashMap<Conjunct, PExp>();
            List<PExp> newTheorems = new LinkedList<PExp>();
            List<Integer> newIndecis = new LinkedList<Integer>();

            if (input.bindSites.size() > 1) {
                //Add the new, transformed conjuncts
                for (PExp newTheorem : myTransformationTemplateConjuncts) {
                    newTheorems.add(newTheorem
                            .substitute(input.freeVariableBindings));
                    newIndecis.add(null);
                }
            }
            else {
                Site bindSite = input.bindSites.values().iterator().next();

                PExp transformed =
                        myTransformationTemplate
                                .substitute(input.freeVariableBindings);
                PExp topLevelTransformed =
                        bindSite.root.exp.withSiteAltered(bindSite
                                .pathIterator(), transformed);

                List<PExp> topLevelConjuncts =
                        topLevelTransformed.splitIntoConjuncts();

                //Add the new theorems
                for (PExp c : topLevelConjuncts) {
                    newTheorems.add(c);
                    newIndecis.add(null);
                }
            }

            return new GeneralApplication(input.bindSites.values(), newValues,
                    newTheorems, newIndecis,
                    ExpandAntecedentBySubstitution.this, myTheorem);
        }
    }

    @Override
    public String toString() {
        return "" + myTheorem;
    }
}
