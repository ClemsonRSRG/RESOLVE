/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.AbstractBinder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.IntroduceLocalTheoremStep;
import edu.clemson.cs.r2jt.proving2.utilities.InductiveSiteIteratorIterator;
import edu.clemson.cs.r2jt.typeandpopulate.NoSolutionException;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
    private final PExp myTransformationTemplate;
    private final PExp myTheorem;

    public ExpandAntecedentBySubstitution(PExp matchPattern,
            PExp transformationTemplate, PExp theorem) {
        myMatchPattern = matchPattern;
        myTransformationTemplate = transformationTemplate;
        myTheorem = theorem;
    }

    public PExp getMatchPattern() {
        return myMatchPattern;
    }

    public PExp getTransformationTemplate() {
        return myTransformationTemplate;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<BindResult> bindResults =
                m
                        .bind(Collections
                                .singleton((Binder) new SkipOneTopLevelAntecedentBinder(
                                        myMatchPattern)));

        return new LazyMappingIterator<BindResult, Application>(bindResults,
                BIND_RESULT_TO_APPLICATION);
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

    private class BindResultToApplication
            implements
                Mapping<BindResult, Application> {

        @Override
        public Application map(BindResult input) {
            return new ExpandAntecedentBySubstitutionApplication(
                    input.bindSites.values().iterator().next(),
                    input.freeVariableBindings);
        }
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

        private final PExp myTheoremToSkip;
        private final Iterator<Site> myBaseIterator;
        private Site myNextReturn;

        public SkipOneTopLevelAntecedentIterator(Iterator<Site> sites,
                PExp theoremToSkip) {
            myTheoremToSkip = theoremToSkip;
            myBaseIterator = sites;

            setUpNext();
        }

        private void setUpNext() {
            if (myBaseIterator.hasNext()) {
                myNextReturn = myBaseIterator.next();

                if (myNextReturn.exp.equals(myTheoremToSkip)) {
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

    private class ExpandAntecedentBySubstitutionApplication
            implements
                Application {

        private final Set<Theorem> myBindTheorem;
        private final Site myBindSite;
        private final Map<PExp, PExp> myBindings;

        public ExpandAntecedentBySubstitutionApplication(Site bindSite,
                Map<PExp, PExp> bindings) {
            myBindSite = bindSite;
            myBindings = bindings;

            try {
                myBindTheorem =
                        Collections.singleton(bindSite.getRootTheorem());
            }
            catch (NoSolutionException nse) {
                //In this case we're certain that the bind site is associated
                //with a local theorem
                throw new RuntimeException(nse);
            }
        }

        @Override
        public String description() {
            return "Add "
                    + myBindSite.root.exp.withSiteAltered(myBindSite
                            .pathIterator(), myTransformationTemplate
                            .substitute(myBindings));
        }

        @Override
        public void apply(PerVCProverModel m) {
            PExp transformed = myTransformationTemplate.substitute(myBindings);
            PExp topLevelTransformed =
                    myBindSite.root.exp.withSiteAltered(myBindSite
                            .pathIterator(), transformed);

            LocalTheorem newTheorem =
                    m.addLocalTheorem(topLevelTransformed, null, false);

            m.addProofStep(new IntroduceLocalTheoremStep(newTheorem,
                    ExpandAntecedentBySubstitution.this, myBindTheorem));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(myBindSite);
        }
    }

    @Override
    public String toString() {
        return "" + myTheorem;
    }
}
