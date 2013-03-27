/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ModifyAntecedentStep implements ProofStep {

    private final Site myOriginalSite;
    private final Transformation myTransformation;
    private final Site myFinalSite;

    public ModifyAntecedentStep(Site originalSite, Site finalSite,
            Transformation transformation) {
        myOriginalSite = originalSite;
        myTransformation = transformation;
        myFinalSite = finalSite;
    }

    @Override
    public Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public Set<Site> getPrerequisiteSites() {
        return Collections.singleton(myOriginalSite.root);
    }

    @Override
    public Set<Site> getAffectedSites() {
        return Collections.singleton(myFinalSite.root);
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
