/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.utilities.Predicate;
import edu.clemson.cs.r2jt.utilities.FlagManager;
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
