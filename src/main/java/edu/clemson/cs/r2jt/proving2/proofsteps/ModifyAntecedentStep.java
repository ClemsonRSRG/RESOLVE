/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ModifyAntecedentStep extends AbstractProofStep {

    private final Site myOriginalSite;
    private final Site myFinalSite;

    public ModifyAntecedentStep(Site originalSite, Site finalSite,
            Transformation t, Application a) {
        super(t, a);

        myOriginalSite = originalSite;
        myFinalSite = finalSite;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.alterSite(myFinalSite, myOriginalSite.exp);
    }

    @Override
    public String toString() {
        return "" + getTransformation();
    }
}
