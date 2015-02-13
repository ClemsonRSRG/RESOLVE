/**
 * ProbationaryApplication.java
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
package edu.clemson.cs.r2jt.rewriteprover.automators;

import edu.clemson.cs.r2jt.rewriteprover.applications.Application;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.rewriteprover.utilities.Predicate;
import edu.clemson.cs.r2jt.misc.FlagManager;
import java.util.Deque;

/**
 *
 * @author hamptos
 */
public class ProbationaryApplication implements Automator {

    private final Application myApplication;
    private final Predicate<ProofStep> myPredicate;
    private boolean myAppliedFlag = false;
    private boolean myChangeStuckFlag = true;

    public ProbationaryApplication(Application application,
            Predicate<ProofStep> p) {

        myApplication = application;
        myPredicate = p;
    }

    public boolean changeStuck() {
        return myChangeStuckFlag;
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (!myAppliedFlag) {
            myApplication.apply(model);
            myAppliedFlag = true;
        }
        else {
            if (!myPredicate.test(model.getLastProofStep())) {
                model.undoLastProofStep();
                myChangeStuckFlag = false;
                if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                    System.out
                            .println("ProbationaryApplication - Rolling back change");
                }
            }

            stack.pop();
        }
    }
}
