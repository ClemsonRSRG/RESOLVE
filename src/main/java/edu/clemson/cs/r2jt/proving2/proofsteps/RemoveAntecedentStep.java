/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class RemoveAntecedentStep implements ProofStep {

    private final LocalTheorem myOriginalTheorem;
    private final int myIndex;
    private final Transformation myTransformation;

    public RemoveAntecedentStep(LocalTheorem originalTheorem, int index,
            Transformation t) {
        myOriginalTheorem = originalTheorem;
        myTransformation = t;
        myIndex = index;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.addLocalTheorem(myOriginalTheorem.getAssertion(), myOriginalTheorem
                .getJustification(), myOriginalTheorem.amTryingToProveThis(),
                myIndex);
    }

    @Override
    public String toString() {
        return "Remove " + myOriginalTheorem.getAssertion();
    }
}
