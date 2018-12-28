/*
 * ApplyAll.java
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
package edu.clemson.cs.r2jt.rewriteprover.automators;

import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Deque;
import java.util.Iterator;

/**
 *
 * @author hamptos
 */
public class ApplyAll implements Automator {

    private final Transformation myTransformation;
    private int myApplicationCount;

    public ApplyAll(Transformation t) {
        myTransformation = t;
    }

    public int getApplicationCount() {
        return myApplicationCount;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Iterator<Application> applications =
                myTransformation.getApplications(model);

        if (applications.hasNext()) {
            applications.next().apply(model);
            myApplicationCount++;
        }
        else {
            stack.pop();
        }
    }

    @Override
    public String toString() {
        return "Apply all: " + myTransformation;
    }
}
