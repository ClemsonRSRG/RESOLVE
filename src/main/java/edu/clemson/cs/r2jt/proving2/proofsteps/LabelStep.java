/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class LabelStep implements ProofStep {

    private final String myLabel;
    private final Transformation myTransformation;

    public LabelStep(String label, Transformation t) {
        myLabel = label;
        myTransformation = t;
    }

    @Override
    public Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public void undo(PerVCProverModel m) {

    }

    @Override
    public String toString() {
        return myLabel;
    }

    @Override
    public Set<Site> getPrerequisiteSites() {
        return Collections.emptySet();
    }

    @Override
    public Set<Site> getAffectedSites() {
        return Collections.emptySet();
    }
}
