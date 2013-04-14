/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.InductiveConsequentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyConsequentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ReplaceTheoremInConsequentWithTrue implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private final Theorem myTheorem;
    private final PExp myTheoremAssertion;

    public ReplaceTheoremInConsequentWithTrue(Theorem theorem) {
        myTheorem = theorem;
        myTheoremAssertion = myTheorem.getAssertion();
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Set<Binder> binders = new HashSet<Binder>();
        binders.add(new InductiveConsequentBinder(myTheoremAssertion));

        return new LazyMappingIterator<BindResult, Application>(
                m.bind(binders), BIND_RESULT_TO_APPLICATION);
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
        return myTheoremAssertion.getFunctionApplications().size() * -1;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return false;
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        return myTheoremAssertion.getSymbolNames();
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return Collections.singleton("true");
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
            return new ReplaceTheoremInConsequentWithTrueApplication(
                    input.bindSites.values());
        }
    }

    private class ReplaceTheoremInConsequentWithTrueApplication
            implements
                Application {

        private final Site myBindSite;
        private Site myFinalSite;

        public ReplaceTheoremInConsequentWithTrueApplication(
                Collection<Site> bindSites) {
            myBindSite = bindSites.iterator().next();
        }

        @Override
        public String description() {
            return "To true.";
        }

        @Override
        public void apply(PerVCProverModel m) {
            m.alterSite(myBindSite, m.getTrue());

            myFinalSite =
                    new Site(m, myBindSite.conjunct, myBindSite.path, m
                            .getTrue());

            m.addProofStep(new ModifyConsequentStep(myBindSite, myFinalSite,
                    ReplaceTheoremInConsequentWithTrue.this, this, Collections
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
        return "" + myTheorem;
    }
}
