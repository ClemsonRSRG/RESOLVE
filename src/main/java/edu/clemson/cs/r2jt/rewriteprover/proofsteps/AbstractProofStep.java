/**
 * AbstractProofStep.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public abstract class AbstractProofStep implements ProofStep {

    private final Transformation myTransformation;
    private final Application myApplication;
    private final Collection<Site> myBoundSites;

    private Set<Conjunct> myBoundConjuncts;

    public AbstractProofStep(Transformation t, Application a,
            Collection<Site> boundSites) {
        myTransformation = t;
        myApplication = a;
        myBoundSites = boundSites;
    }

    @Override
    public final Transformation getTransformation() {
        return myTransformation;
    }

    @Override
    public final Application getApplication() {
        return myApplication;
    }

    @Override
    public final Set<Conjunct> getPrerequisiteConjuncts() {
        return myApplication.getPrerequisiteConjuncts();
    }

    @Override
    public final Set<Conjunct> getBoundConjuncts() {
        if (myBoundConjuncts == null) {
            myBoundConjuncts = new HashSet<Conjunct>();

            if (myBoundSites != null) {
                for (Site s : myBoundSites) {
                    myBoundConjuncts.add(s.conjunct);
                }
            }

            myBoundConjuncts = Collections.unmodifiableSet(myBoundConjuncts);
        }

        return myBoundConjuncts;
    }

    @Override
    public final Set<Conjunct> getAffectedConjuncts() {
        return myApplication.getAffectedConjuncts();
    }

    @Override
    public final Set<Site> getAffectedSites() {
        return myApplication.getAffectedSites();
    }
}
