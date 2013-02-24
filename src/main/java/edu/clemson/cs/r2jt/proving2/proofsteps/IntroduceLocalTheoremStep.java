/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class IntroduceLocalTheoremStep implements ProofStep {

    private final LocalTheorem myLocalTheorem;
    private final Transformation myTransformation;
    private final Set<Theorem> myPrerequisiteTheorems;

    public IntroduceLocalTheoremStep(LocalTheorem theorem,
            Transformation transformation, Set<Theorem> prerequisiteTheorems) {
        myLocalTheorem = theorem;
        myTransformation = transformation;
        myPrerequisiteTheorems = prerequisiteTheorems;
    }

    public Set<Theorem> getPrerequisiteTheorems() {
        return myPrerequisiteTheorems;
    }

    public LocalTheorem getIntroducedTheorem() {
        return myLocalTheorem;
    }

    public Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public String toString() {
        return "" + myTransformation;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.removeLocalTheorem(myLocalTheorem);
    }

}
