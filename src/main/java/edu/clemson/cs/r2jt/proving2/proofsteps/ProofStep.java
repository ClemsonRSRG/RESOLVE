/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public interface ProofStep {

    public void undo(PerVCProverModel m);

    public Transformation getTransformation();

    public Set<Site> getPrerequisiteSites();

    public Set<Site> getAffectedSites();
}
