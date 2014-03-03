/**
 * RemoveConsequentStep.java
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
import edu.clemson.cs.r2jt.proving2.model.Consequent;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Set;

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
