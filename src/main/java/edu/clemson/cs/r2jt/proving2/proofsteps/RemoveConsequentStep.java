/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Consequent;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class RemoveConsequentStep extends AbstractProofStep {

    private final Consequent myConsequent;
    private final int myOriginalIndex;

    public RemoveConsequentStep(Consequent consequent, int originalIndex,
            Transformation t, Application a) {
        super(t, a);

        myOriginalIndex = originalIndex;
        myConsequent = consequent;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.insertConjunct(myConsequent, myOriginalIndex);
    }

    @Override
    public String toString() {
        return "Remove " + getTransformation();
    }
}
