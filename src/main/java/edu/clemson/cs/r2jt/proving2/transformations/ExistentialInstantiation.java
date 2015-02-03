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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.DummyIterator;
import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.Consequent;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.AbstractBinder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.ExistentialInstantiationStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
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
