/*
 * ProofStep.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.proofsteps;

import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
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
