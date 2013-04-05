/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.InductiveAntecedentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyAntecedentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
    private PExp myTransformationTemplate;
    private final Theorem myTheorem;

    public SubstituteInPlaceInAntecedent(Theorem t, PExp tMatchPattern,
            PExp tTransformationTemplate) {

        myTheorem = t;
        myMatchPattern = tMatchPattern;
        myTransformationTemplate = tTransformationTemplate;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<PerVCProverModel.BindResult> bindResults =
                m
                        .bind(Collections
                                .singleton((PerVCProverModel.Binder) new InductiveAntecedentBinder(
                                        myMatchPattern)));

        return new LazyMappingIterator<PerVCProverModel.BindResult, Application>(
                bindResults, BIND_RESULT_TO_APPLICATION);
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

    private class BindResultToApplication
            implements
                Mapping<PerVCProverModel.BindResult, Application> {

        @Override
        public Application map(PerVCProverModel.BindResult input) {
            return new SubstituteInPlaceInAntecedentApplication(input.bindSites
                    .values().iterator().next(), input.freeVariableBindings);
        }

    }

    private class SubstituteInPlaceInAntecedentApplication
            implements
                Application {

        private final Site myBindSite;
        private Site myFinalSite;
        private final Map<PExp, PExp> myBindings;

        public SubstituteInPlaceInAntecedentApplication(Site bindSite,
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

            m.addProofStep(new ModifyAntecedentStep(myBindSite, myFinalSite,
                    SubstituteInPlaceInAntecedent.this, this));
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
