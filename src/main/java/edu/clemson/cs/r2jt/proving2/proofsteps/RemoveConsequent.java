/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class RemoveConsequent implements ProofStep {

    private final PExp myConsequent;
    private final int myIndex;
    private final Transformation myTransformation;

    public RemoveConsequent(PExp consequent, int index, Transformation t) {
        myConsequent = consequent;
        myIndex = index;
        myTransformation = t;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.addConsequent(myConsequent, myIndex);
    }

    @Override
    public String toString() {
        return "" + myTransformation;
    }
}
