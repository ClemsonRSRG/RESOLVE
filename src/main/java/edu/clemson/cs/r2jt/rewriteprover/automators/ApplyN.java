/*
 * ApplyN.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.automators;

import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Deque;

/**
 *
 * @author hamptos
 */
public class ApplyN implements Automator {

    private Transformation myTransformation;
    private int myRemainingApplications;

    public ApplyN(Transformation t, int count) {
        myTransformation = t;
        myRemainingApplications = count;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myRemainingApplications == 0) {
            stack.pop();
        }
        else {
            myTransformation.getApplications(model).next().apply(model);
        }

        myRemainingApplications--;
    }

}
