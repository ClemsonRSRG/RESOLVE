/**
 * IntroduceLocalTheoremStep.java
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
import edu.clemson.cs.r2jt.rewriteprover.model.LocalTheorem;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class IntroduceLocalTheoremStep extends AbstractProofStep {

    private final LocalTheorem myIntroducedTheorem;
    private final Set<Theorem> myPrerequisiteTheorems;

    public IntroduceLocalTheoremStep(LocalTheorem introducedTheorem,
            Set<Theorem> prerequisiteTheorems, Transformation t, Application a,
            Collection<Site> boundSites) {
        super(t, a, boundSites);

        myIntroducedTheorem = introducedTheorem;
        myPrerequisiteTheorems =
                Collections.unmodifiableSet(prerequisiteTheorems);
    }

    public Set<Theorem> getPrerequisiteTheorems() {
        return new HashSet<Theorem>(myPrerequisiteTheorems);
    }

    public LocalTheorem getIntroducedTheorem() {
        return myIntroducedTheorem;
    }

    @Override
    public String toString() {
        return "" + getTransformation();
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.removeLocalTheorem(myIntroducedTheorem);
    }

}
