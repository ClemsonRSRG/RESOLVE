/*
 * Restore.java
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
import java.util.Deque;

/**
 * <p>
 * The <code>Restore</code> automator simply waits for the heartbeat before
 * undoing any proof steps
 * that have occurred since it was created and then popping itself off the
 * stack.
 * </p>
 */
public class Restore implements Automator {

    private final int myOriginalProofStepCount;
    private final MainProofLevel myMainProofLevel;

    public Restore(PerVCProverModel m, MainProofLevel main) {
        myOriginalProofStepCount = m.getProofSteps().size();
        myMainProofLevel = main;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {

        myMainProofLevel.prepForRestore();

        int currentProofStepCount = model.getProofSteps().size();
        int additionProofStepCount =
                currentProofStepCount - myOriginalProofStepCount;

        for (int i = 0; i < additionProofStepCount; i++) {
            model.undoLastProofStep();
        }

        stack.pop();
    }

}
