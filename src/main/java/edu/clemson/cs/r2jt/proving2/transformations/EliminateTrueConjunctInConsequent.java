/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.TopLevelConsequentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.RemoveConsequent;
import edu.clemson.cs.r2jt.utilities.Mapping;
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
        Iterator<BindResult> results =
                m.bind(Collections
                        .singleton((Binder) new TopLevelConsequentBinder(m
                                .getTrue())));

        return new LazyMappingIterator<BindResult, Application>(results,
                BIND_RESULT_TO_APPLICATION);
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
            m.removeConsequent(mySite.index);

            m.addProofStep(new RemoveConsequent(mySite.exp, mySite.index,
                    EliminateTrueConjunctInConsequent.this));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(mySite);
        }
    }

    @Override
    public String toString() {
        return "Eliminate true conjunct";
    }
}
