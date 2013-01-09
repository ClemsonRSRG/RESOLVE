/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class IntroduceLocalTheoremStep implements ProofStep {

    private final LocalTheorem myLocalTheorem;
    private final Transformation myTransformation;

    public IntroduceLocalTheoremStep(LocalTheorem theorem,
            Transformation transformation) {
        myLocalTheorem = theorem;
        myTransformation = transformation;
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
