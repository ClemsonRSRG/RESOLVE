/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class ModifyAntecedentStep implements ProofStep {

    private final Site myOriginalSite;
    private final Transformation myTransformation;

    public ModifyAntecedentStep(Site originalSite, Transformation transformation) {
        myOriginalSite = originalSite;
        myTransformation = transformation;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.alterSite(myOriginalSite, myOriginalSite.root.exp);
    }

    @Override
    public String toString() {
        return "" + myTransformation;
    }
}
