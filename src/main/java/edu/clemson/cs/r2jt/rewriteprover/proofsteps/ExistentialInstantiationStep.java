/*
 * ExistentialInstantiationStep.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.proofsteps;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.Consequent;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class ExistentialInstantiationStep extends AbstractProofStep {

    private final Conjunct myExistentialConsequent;
    private final List<PExp> myOriginalValues;
    private final int myExistentialConsequentIndex;

    public ExistentialInstantiationStep(Transformation t, Application a,
            Conjunct existentialConjunct, int existentialConjunctIndex,
            List<PExp> originalValues, Collection<Site> boundSites) {
        super(t, a, boundSites);

        myExistentialConsequent = existentialConjunct;
        myExistentialConsequentIndex = existentialConjunctIndex;
        myOriginalValues = originalValues;
    }

    @Override
    public void undo(PerVCProverModel m) {
        m.insertConjunct(myExistentialConsequent, myExistentialConsequentIndex);

        Iterator<PExp> originals = myOriginalValues.iterator();
        for (Consequent c : m.getConsequentList()) {
            m.alterSite(c.toSite(m), originals.next());
        }
    }
}
