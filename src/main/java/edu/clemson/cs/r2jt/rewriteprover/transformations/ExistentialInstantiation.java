/*
 * ExistentialInstantiation.java
 * ---------------------------------
 * Copyright (c) 2017
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
import edu.clemson.cs.r2jt.rewriteprover.iterators.DummyIterator;
import edu.clemson.cs.r2jt.rewriteprover.iterators.LazyMappingIterator;
import edu.clemson.cs.r2jt.rewriteprover.absyn.BindingException;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.Consequent;
import edu.clemson.cs.r2jt.rewriteprover.model.LocalTheorem;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.AbstractBinder;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.ExistentialInstantiationStep;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import java.util.Collections;
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
public class ExistentialInstantiation implements Transformation {

    public static final ExistentialInstantiation INSTANCE =
            new ExistentialInstantiation();

    private static final BindingException BINDING_EXCEPTION =
            new BindingException();

    private ExistentialInstantiation() {

    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {

        Iterator<BindResult> bindResults =
                DummyIterator.<BindResult> getInstance();

        for (Consequent c : m.getConsequentList()) {
            if (c.getExpression().containsExistential()) {
                bindResults =
                        new ChainingIterator(
                                bindResults,
                                m
                                        .bind(Collections
                                                .singleton((Binder) new ConsequentBasedBinder(
                                                        c))));
            }
        }

        return new LazyMappingIterator(bindResults,
                new BindResultToApplication(m));
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
        return -2;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return false;
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.EQUIVALENT;
    }

    @Override
    public String toString() {
        return "Existential Instantiation";
    }

    @Override
    public String getKey() {
        return this.getClass().getName();
    }

    public static class ConsequentBasedBinder extends AbstractBinder {

        private final Consequent myConsequent;

        public ConsequentBasedBinder(Consequent c) {
            super(c.getExpression());
            myConsequent = c;
        }

        public Consequent getConsequent() {
            return myConsequent;
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            return m.topLevelAntecedentAndGlobalTheoremSiteIterator();
        }
    }

    private class BindResultToApplication
            implements
                Mapping<BindResult, Application> {

        private final PerVCProverModel myModel;

        public BindResultToApplication(PerVCProverModel m) {
            myModel = m;
        }

        @Override
        public Application map(BindResult input) {
            return new ExistentialInstantiationApplication(myModel,
                    ((ConsequentBasedBinder) input.bindSites.keySet()
                            .iterator().next()).getConsequent(),
                    input.bindSites.values().iterator().next(),
                    input.freeVariableBindings);
        }
    }

    private class ExistentialInstantiationApplication implements Application {

        private final Site myBindSite;
        private final Map<PExp, PExp> myBindings;
        private final Consequent myExistentialConsequent;
        private final Site myExistentialConsequentSite;

        private final Set<Conjunct> myAffectedConsequents =
                new HashSet<Conjunct>();
        private final Set<Site> myAffectedSites = new HashSet<Site>();

        public ExistentialInstantiationApplication(PerVCProverModel m,
                Consequent existentialConsequent, Site bindSite,
                Map<PExp, PExp> bindings) {

            myExistentialConsequent = existentialConsequent;
            myExistentialConsequentSite = existentialConsequent.toSite(m);
            myBindings = bindings;
            myBindSite = bindSite;
        }

        @Override
        public void apply(PerVCProverModel m) {
            int index = m.removeConjunct(myExistentialConsequent);

            List<PExp> originals = new LinkedList<PExp>();
            myAffectedConsequents.clear();
            for (Consequent c : m.getConsequentList()) {
                myAffectedConsequents.add(c);
                myAffectedSites.add(c.toSite(m));
                originals.add(m.alterSite(c.toSite(m), c.getExpression()
                        .substitute(myBindings)));
            }

            m.addProofStep(new ExistentialInstantiationStep(
                    ExistentialInstantiation.this, this,
                    myExistentialConsequent, index, originals, Collections
                            .singleton(myBindSite)));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            Set<Site> result = new HashSet<Site>();
            result.add(myBindSite);
            result.add(myExistentialConsequentSite);

            return result;
        }

        @Override
        public String description() {
            return "Instantiate existential: " + myBindings;
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            Set<Conjunct> result = new HashSet<Conjunct>();

            result.add(myExistentialConsequent);
            result.add(myBindSite.conjunct);

            return result;
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return myAffectedConsequents;
        }

        @Override
        public Set<Site> getAffectedSites() {
            return myAffectedSites;
        }
    }

    private static class LocalTheoremCaster
            implements
                Mapping<LocalTheorem, Theorem> {

        public final static LocalTheoremCaster INSTANCE =
                new LocalTheoremCaster();

        @Override
        public Theorem map(LocalTheorem input) {
            return (Theorem) input;
        }
    }
}
