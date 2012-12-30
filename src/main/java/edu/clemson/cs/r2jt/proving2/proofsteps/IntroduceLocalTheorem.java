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
public class IntroduceLocalTheorem implements ProofStep {

    private final LocalTheorem myLocalTheorem;
    private final Transformation myTransformation;
    
    public IntroduceLocalTheorem(LocalTheorem theorem, 
            Transformation transformation) {
        myLocalTheorem = theorem;
        myTransformation = transformation;
    }
    
    @Override
    public void undo(PerVCProverModel m) {
        m.removeLocalTheorem(myLocalTheorem);
    }
    
}
