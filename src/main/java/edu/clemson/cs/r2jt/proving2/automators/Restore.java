/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import java.util.Deque;

/**
 * <p>The <code>Restore</code> automator simply waits for the heartbeat before
 * undoing any proof steps that have occurred since it was created and then
 * popping itself off the stack.</p>
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
