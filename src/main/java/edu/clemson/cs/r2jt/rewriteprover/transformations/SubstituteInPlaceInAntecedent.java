/*
 * SubstituteInPlaceInAntecedent.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.transformations;

import edu.clemson.cs.r2jt.rewriteprover.iterators.ChainingIterator;
import edu.clemson.cs.r2jt.rewriteprover.iterators.LazyMappingIterator;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.applications.GeneralApplication;
import edu.clemson.cs.r2jt.rewriteprover.model.AtLeastOneLocalTheoremBinder;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.InductiveAntecedentBinder;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
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
public class SubstituteInPlaceInAntecedent implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private PExp myMatchPattern;
    private List<PExp> myMatchPatternConjuncts;
    private int myMatchPatternConjunctsSize;

    private PExp myTransformationTemplate;
    private List<PExp> myTransformationTemplateConjuncts;
    private final Theorem myTheorem;

    public SubstituteInPlaceInAntecedent(Theorem t, PExp tMatchPattern,
            PExp tTransformationTemplate) {

        myTheorem = t;

        myMatchPattern = tMatchPattern;
        myMatchPatternConjuncts = tMatchPattern.splitIntoConjuncts();
        myMatchPatternConjunctsSize = myMatchPatternConjuncts.size();

        myTransformationTemplate = tTransformationTemplate;
        myTransformationTemplateConjuncts =
                tTransformationTemplate.splitIntoConjuncts();
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<Application> result;

        Iterator<BindResult> bindResults =
                m.bind(Collections
                        .singleton((Binder) new InductiveAntecedentBinder(
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
        return false;
    }

    @Override
    public boolean couldAffectConsequent() {
        return true;
    }

    @Override
    public int functionApplicationCountDelta() {
        return myTransformationTemplate.getFunctionApplications().size()
                - myMatchPattern.getFunctionApplications().size();
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        Set<PSymbol> introduced =
                myTransformationTemplate.getQuantifiedVariables();

        introduced.removeAll(myMatchPattern.getFunctionApplications());

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
    public Transformation.Equivalence getEquivalence() {
        return Transformation.Equivalence.EQUIVALENT;
    }

    @Override
    public String getKey() {
        return myTheorem.getAssertion() + " " + this.getClass().getName();
    }

    private class BindResultToApplication
            implements
                Mapping<PerVCProverModel.BindResult, Application> {

        @Override
        public Application map(PerVCProverModel.BindResult input) {
            Map<Conjunct, PExp> newValues = new HashMap<Conjunct, PExp>();
            List<PExp> newTheorems = new LinkedList<PExp>();
            List<Integer> newIndecis = new LinkedList<Integer>();

            if (input.bindSites.size() > 1) {

                //Delete the existing things we matched
                int maxBindIndex = -1;
                int curBindIndex;
                for (Site s : input.bindSites.values()) {
                    if (s.conjunct.editable()) {
                        curBindIndex =
                                s.getModel().getConjunctIndex(s.conjunct);
                        if (curBindIndex > maxBindIndex) {
                            maxBindIndex = curBindIndex;
                        }

                        //We're going to want to delete each of these
                        newValues.put(s.conjunct, null);
                    }
                }

                //Add the new, transformed conjuncts
                for (PExp newTheorem : myTransformationTemplateConjuncts) {
                    newTheorems.add(newTheorem
                            .substitute(input.freeVariableBindings));
                    newIndecis.add((Integer) maxBindIndex);
                    maxBindIndex++;
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

                if (topLevelConjuncts.size() == 1) {
                    //Change the value in place
                    newValues.put(bindSite.conjunct, topLevelTransformed);
                }
                else {
                    //Delete the current conjunct
                    newValues.put(bindSite.conjunct, null);

                    //Add the new ones
                    int index =
                            bindSite.getModel().getConjunctIndex(
                                    bindSite.conjunct);
                    for (PExp c : topLevelConjuncts) {
                        newTheorems.add(c);
                        newIndecis.add(index);
                        index++;
                    }
                }
            }

            return new GeneralApplication(input.bindSites.values(), newValues,
                    newTheorems, newIndecis,
                    SubstituteInPlaceInAntecedent.this, myTheorem);
        }
    }

    @Override
    public String toString() {
        return "" + myMatchPattern + " = " + myTransformationTemplate;
    }
}
