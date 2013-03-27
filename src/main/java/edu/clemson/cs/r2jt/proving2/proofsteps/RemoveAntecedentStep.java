/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class RemoveAntecedentStep implements ProofStep {

    private final LocalTheorem myOriginalTheorem;
    private final int myIndex;
    private final Transformation myTransformation;
    private final Site mySite;

    public RemoveAntecedentStep(LocalTheorem originalTheorem, Site site,
            Transformation t) {
        myOriginalTheorem = originalTheorem;
        myTransformation = t;
        myIndex = site.index;
        mySite = site;
    }

    @Override
    public Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public Set<Site> getPrerequisiteSites() {
        return Collections.singleton(mySite);
    }

    @Override
    public Set<Site> getAffectedSites() {
        return Collections.emptySet();
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
