/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.RemoveAntecedentStep;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class RemoveAntecedent implements Transformation {

    private final Site mySite;
    private LocalTheorem myLocalTheoremWhenRemoved;

    public RemoveAntecedent(PerVCProverModel model, int index) {
        mySite =
                new Site(model, Site.Section.ANTECEDENTS, index,
                        Collections.EMPTY_LIST, model.getLocalTheorem(index)
                                .getAssertion());
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        return Collections.singletonList(
                (Application) new RemoveAntecedentApplication()).iterator();
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
        return mySite.exp.getFunctionApplications().size() * -1;
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
        return Equivalence.WEAKER;
    }

    private class RemoveAntecedentApplication implements Application {

        @Override
        public void apply(PerVCProverModel m) {
            myLocalTheoremWhenRemoved = m.removeLocalTheorem(mySite.index);
            m.addProofStep(new RemoveAntecedentStep(myLocalTheoremWhenRemoved,
                    mySite.index, RemoveAntecedent.this));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(mySite);
        }

        @Override
        public String description() {
            return "Remove " + mySite.exp;
        }
    }

    @Override
    public String toString() {
        String result = "Remove ";

        if (myLocalTheoremWhenRemoved == null) {
            result += mySite.exp;
        }
        else {
            result += myLocalTheoremWhenRemoved.getAssertion();
        }

        return result;
    }
}
