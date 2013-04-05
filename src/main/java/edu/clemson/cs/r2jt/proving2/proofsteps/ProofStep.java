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
import java.util.Set;

/**
 *
 * @author hamptos
 */
public interface ProofStep {

    public void undo(PerVCProverModel m);

    public Transformation getTransformation();

    public Application getApplication();

    public Set<Conjunct> getPrerequisiteConjuncts();

    public Set<Conjunct> getAffectedConjuncts();

    public Set<Site> getAffectedSites();
}
