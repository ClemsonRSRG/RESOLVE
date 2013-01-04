/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.AbstractBinder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.IntroduceLocalTheorem;
import edu.clemson.cs.r2jt.proving2.utilities.InductiveSiteIteratorIterator;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
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

        private final Site myBindSite;
        private final Map<PExp, PExp> myBindings;

        public ExpandAntecedentBySubstitutionApplication(Site bindSite,
                Map<PExp, PExp> bindings) {
            myBindSite = bindSite;
            myBindings = bindings;
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

            m.addProofStep(new IntroduceLocalTheorem(newTheorem,
                    ExpandAntecedentBySubstitution.this));
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
