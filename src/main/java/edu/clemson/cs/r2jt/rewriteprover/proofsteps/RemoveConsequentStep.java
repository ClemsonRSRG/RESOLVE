/**
 * RemoveConsequentStep.java
 * ---------------------------------
 * Copyright (c) 2015
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
import edu.clemson.cs.r2jt.rewriteprover.model.Consequent;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Collection;

/**
 *
 * @author hamptos
 */
public class RemoveConsequentStep extends AbstractProofStep {

    private final Consequent myConsequent;
    private final int myOriginalIndex;

    public RemoveConsequentStep(Consequent consequent, int originalIndex,
            Transformation t, Application a, Collection<Site> boundSites) {
        super(t, a, boundSites);

        myOriginalIndex = originalIndex;
        myConsequent = consequent;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.insertConjunct(myConsequent, myOriginalIndex);
    }

    @Override
    public String toString() {
        return "Remove " + getTransformation();
    }
}
