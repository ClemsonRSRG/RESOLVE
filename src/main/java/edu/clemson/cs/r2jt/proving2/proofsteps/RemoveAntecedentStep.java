/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class RemoveAntecedentStep extends AbstractProofStep {

    private final LocalTheorem myOriginalTheorem;
    private final int myOriginalIndex;

    public RemoveAntecedentStep(LocalTheorem originalTheorem,
            int originalIndex, Transformation t, Application a) {
        super(t, a);

        myOriginalIndex = originalIndex;
        myOriginalTheorem = originalTheorem;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.insertConjunct(myOriginalTheorem, myOriginalIndex);
    }

    @Override
    public String toString() {
        return "Remove " + myOriginalTheorem.getAssertion();
    }
}
