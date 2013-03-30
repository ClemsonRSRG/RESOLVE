/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving2.AutomatedProver;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.LabelStep;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class NoOpLabel implements Transformation {

    private final String myLabel;
    private final AutomatedProver myProver;

    public NoOpLabel(AutomatedProver p, String label) {
        myLabel = label;
        myProver = p;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        return Collections.singletonList(
                (Application) new NoOpLabelApplication()).iterator();
    }

    @Override
    public boolean couldAffectAntecedent() {
        return false;
    }

    @Override
    public boolean couldAffectConsequent() {
        return false;
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
        return myLabel;
    }

    private class NoOpLabelApplication implements Application {

        @Override
        public void apply(PerVCProverModel m) {
            m.addProofStep(new LabelStep(myLabel, NoOpLabel.this, this));

            //Useful for debugging--pauses automated prover when a label is 
            //reached
            //myProver.markToPause();  
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.EMPTY_SET;
        }

        @Override
        public String description() {
            return "Label with \"" + myLabel + "\"";
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            return Collections.EMPTY_SET;
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return Collections.EMPTY_SET;
        }
    }
}
