/*
 * SubstituteInPlaceInConsequent.java
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

import edu.clemson.cs.r2jt.rewriteprover.iterators.LazyMappingIterator;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.InductiveConsequentBinder;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.ModifyConsequentStep;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class SubstituteInPlaceInConsequent implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private PExp myMatchPattern;
    private PExp myTransformationTemplate;
    private final Theorem myTheorem;

    public SubstituteInPlaceInConsequent(Theorem t, PExp tMatchPattern,
            PExp tTransformationTemplate) {

        myTheorem = t;
        myMatchPattern = tMatchPattern;
        myTransformationTemplate = tTransformationTemplate;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<PerVCProverModel.BindResult> bindResults =
                m.bind(Collections
                        .singleton((Binder) new InductiveConsequentBinder(
                                myMatchPattern)));

        return new LazyMappingIterator<BindResult, Application>(bindResults,
                BIND_RESULT_TO_APPLICATION);
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

    public PExp getPattern() {
        return myMatchPattern;
    }

    public PExp getReplacement() {
        return myTransformationTemplate;
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.EQUIVALENT;
    }

    @Override
    public String getKey() {
        return myTheorem.getAssertion() + " " + this.getClass().getName();
    }

    private class BindResultToApplication
            implements
                Mapping<BindResult, Application> {

        @Override
        public Application map(BindResult input) {
            return new SubstituteInPlaceInConsequentApplication(input.bindSites
                    .values().iterator().next(), input.freeVariableBindings);
        }

    }

    private class SubstituteInPlaceInConsequentApplication
            implements
                Application {

        private final Site myBindSite;
        private Site myFinalSite;

        private final Map<PExp, PExp> myBindings;

        public SubstituteInPlaceInConsequentApplication(Site bindSite,
                Map<PExp, PExp> bindings) {
            myBindSite = bindSite;
            myBindings = bindings;
        }

        @Override
        public String description() {
            return "To " + myTransformationTemplate.substitute(myBindings);
        }

        @Override
        public void apply(PerVCProverModel m) {
            PExp transformed = myTransformationTemplate.substitute(myBindings);
            m.alterSite(myBindSite, transformed);

            myFinalSite =
                    new Site(m, myBindSite.conjunct, myBindSite.path,
                            transformed);

            m.addProofStep(new ModifyConsequentStep(myBindSite, myFinalSite,
                    SubstituteInPlaceInConsequent.this, this, Collections
                            .singleton(myBindSite)));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(myBindSite);
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            Set<Conjunct> result = new HashSet<Conjunct>();

            result.add(myBindSite.conjunct);
            result.add(myTheorem);

            return result;
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return Collections.singleton(myBindSite.conjunct);
        }

        @Override
        public Set<Site> getAffectedSites() {
            return Collections.singleton(myFinalSite);
        }
    }

    @Override
    public String toString() {
        return "" + myMatchPattern + " = " + myTransformationTemplate;
    }
}
