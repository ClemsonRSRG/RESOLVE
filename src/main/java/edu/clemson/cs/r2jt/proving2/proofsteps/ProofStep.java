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

    /**
     * <p>Those conjuncts that must be present for this step to make sense--this 
     * will include any conjuncts containing sites that were bound against, any
     * conjuncts representing the theorem that motivated this step, and possibly
     * others.</p>
     * 
     * @return 
     */
    public Set<Conjunct> getPrerequisiteConjuncts();
    
    /**
     * <p>A subset of the prerequisite conjuncts: those conjuncts that were
     * bound against.</p>
     * 
     * @return 
     */
    public Set<Conjunct> getBoundConjuncts();

    public Set<Conjunct> getAffectedConjuncts();

    /**
     * <p>Any sites that were modified or introduced.</p>
     * 
     * @return 
     */
    public Set<Site> getAffectedSites();
}
