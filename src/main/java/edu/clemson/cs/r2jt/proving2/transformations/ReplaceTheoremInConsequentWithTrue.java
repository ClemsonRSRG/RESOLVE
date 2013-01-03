/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.InductiveConsequentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyConsequent;
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

    private final PExp myTheorem;

    public ReplaceTheoremInConsequentWithTrue(PExp theorem) {
        myTheorem = theorem;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Set<Binder> binders = new HashSet<Binder>();
        binders.add(new InductiveConsequentBinder(myTheorem));

        return new LazyMappingIterator<BindResult, Application>(
                m.bind(binders), BIND_RESULT_TO_APPLICATION);
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

        public ReplaceTheoremInConsequentWithTrueApplication(
                Collection<Site> bindSites) {
            myBindSite = bindSites.iterator().next();
        }

        @Override
        public void apply(PerVCProverModel m) {
            m.alterSite(myBindSite, PExp.trueExp(myTheorem.getType()
                    .getTypeGraph()));

            m.addProofStep(new ModifyConsequent(myBindSite,
                    ReplaceTheoremInConsequentWithTrue.this));
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
