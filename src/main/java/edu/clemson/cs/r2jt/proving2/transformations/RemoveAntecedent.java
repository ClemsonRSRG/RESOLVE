/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
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

    private final LocalTheorem myLocalTheorem;
    private final PerVCProverModel mySourceModel;

    public RemoveAntecedent(PerVCProverModel m, LocalTheorem t) {
        myLocalTheorem = t;
        mySourceModel = m;
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
        return myLocalTheorem.getExpression().getFunctionApplications().size()
                * -1;
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
            m.addProofStep(new RemoveAntecedentStep(myLocalTheorem, m
                    .getConjunctIndex(myLocalTheorem), RemoveAntecedent.this,
                    this));
            m.removeLocalTheorem(myLocalTheorem);
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(myLocalTheorem.toSite(mySourceModel));
        }

        @Override
        public String description() {
            return "Remove " + myLocalTheorem;
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            return Collections.<Conjunct> singleton(myLocalTheorem);
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public String toString() {
        String result = "Remove ";

        if (myLocalTheorem == null) {
            result += myLocalTheorem;
        }
        else {
            result += myLocalTheorem.getAssertion();
        }

        return result;
    }
}
