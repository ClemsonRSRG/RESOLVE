/**
 * RemoveAntecedentStep.java
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
package edu.clemson.cs.r2jt.rewriteprover.proofsteps;

import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.LocalTheorem;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Collection;

/**
 *
 * @author hamptos
 */
public class RemoveAntecedentStep extends AbstractProofStep {

    private final LocalTheorem myOriginalTheorem;
    private final int myOriginalIndex;

    public RemoveAntecedentStep(LocalTheorem originalTheorem,
            int originalIndex, Transformation t, Application a,
            Collection<Site> boundSites) {
        super(t, a, boundSites);

        myOriginalIndex = originalIndex;
        myOriginalTheorem = originalTheorem;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.insertConjunct(myOriginalTheorem, myOriginalIndex);
    }

    @Override
    public String toString() {
        return "Remove " + myOriginalTheorem.getAssertion();
    }
}
