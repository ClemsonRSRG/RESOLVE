/*
 * LabelStep.java
 * ---------------------------------
 * Copyright (c) 2020
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
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class LabelStep extends AbstractProofStep {

    private final String myLabel;
    private final Transformation myTransformation;

    public LabelStep(String label, Transformation t, Application a) {
        super(t, a, null);

        myLabel = label;
        myTransformation = t;
    }

    @Override
    public void undo(PerVCProverModel m) {

    }

    @Override
    public String toString() {
        return myLabel;
    }
}
