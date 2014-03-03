/**
 * ModifyConsequentStep.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ModifyConsequentStep extends AbstractProofStep {

    private final Site myOriginalSite;
    private final Site myFinalSite;

    public ModifyConsequentStep(Site originalSite, Site finalSite,
            Transformation t, Application a, Collection<Site> boundSites) {
        super(t, a, boundSites);

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
