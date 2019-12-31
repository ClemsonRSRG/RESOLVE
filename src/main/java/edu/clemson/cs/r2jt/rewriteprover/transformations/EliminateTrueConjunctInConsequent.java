/*
 * EliminateTrueConjunctInConsequent.java
 * ---------------------------------
 * Copyright (c) 2020
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
import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.Consequent;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.TopLevelConsequentBinder;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.RemoveConsequentStep;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class EliminateTrueConjunctInConsequent implements Transformation {

    public static final EliminateTrueConjunctInConsequent INSTANCE =
            new EliminateTrueConjunctInConsequent();

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private EliminateTrueConjunctInConsequent() {

    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<BindResult> results = m.bind(Collections
                .singleton((Binder) new TopLevelConsequentBinder(m.getTrue())));

        return new LazyMappingIterator<BindResult, Application>(results,
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
        return 0;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return false;
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        return Collections.singleton("true");
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.EQUIVALENT;
    }

    private class BindResultToApplication
            implements
                Mapping<BindResult, Application> {

        @Override
        public Application map(BindResult input) {
            return new EliminateTrueConjunctInConsequentApplication(
                    input.bindSites.values().iterator().next());
        }
    }

    private class EliminateTrueConjunctInConsequentApplication
            implements
                Application {

        private final Site mySite;

        public EliminateTrueConjunctInConsequentApplication(Site s) {
            mySite = s;
        }

        @Override
        public String description() {
            return "Eliminate true.";
        }

        @Override
        public void apply(PerVCProverModel m) {
            m.addProofStep(
                    new RemoveConsequentStep((Consequent) mySite.conjunct,
                            m.getConjunctIndex(mySite.conjunct),
                            EliminateTrueConjunctInConsequent.this, this,
                            Collections.singleton(mySite)));

            m.removeConjunct(mySite.conjunct);
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(mySite);
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            return Collections.singleton(mySite.conjunct);
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return Collections.EMPTY_SET;
        }

        @Override
        public Set<Site> getAffectedSites() {
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public String toString() {
        return "Eliminate true conjunct";
    }

    @Override
    public String getKey() {
        return this.getClass().getName();
    }
}
