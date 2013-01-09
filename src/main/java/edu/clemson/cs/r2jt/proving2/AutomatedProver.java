/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.automators.AntecedentDeveloper;
import edu.clemson.cs.r2jt.proving2.automators.ApplyN;
import edu.clemson.cs.r2jt.proving2.automators.Automator;
import edu.clemson.cs.r2jt.proving2.automators.MainProofLevel;
import edu.clemson.cs.r2jt.proving2.automators.PushSequence;
import edu.clemson.cs.r2jt.proving2.automators.VariablePropagator;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author hamptos
 */
public class AutomatedProver {

    private final PerVCProverModel myModel;
    private final ImmutableList<Theorem> myTheoremLibrary;

    private boolean myRunningFlag = true;

    private final Deque<Automator> myAutomatorStack =
            new ArrayDeque<Automator>(20);

    private boolean myPrepForUIUpdateFlag = false;
    private boolean myTakingStepFlag = false;

    private Thread myWorkerThread;

    public AutomatedProver(PerVCProverModel m,
            ImmutableList<Theorem> theoremLibrary) {
        myModel = m;
        m.setAutomatedProver(this);

        myTheoremLibrary = theoremLibrary;

        List<PExp> truths = new LinkedList<PExp>();
        for (Theorem t : theoremLibrary) {
            truths.add(t.getAssertion());
        }

        List<Transformation> transformations = new LinkedList<Transformation>();
        List<Transformation> theoremTransformations;
        for (Theorem t : theoremLibrary) {
            theoremTransformations = t.getTransformations();

            for (Transformation transformation : theoremTransformations) {
                if (!transformation.couldAffectAntecedent()) {
                    transformations.add(transformation);
                }
            }
        }

        List<Automator> steps = new LinkedList<Automator>();
        steps.add(new VariablePropagator());
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Propagating Variables ---"), 1));
        steps.add(new AntecedentDeveloper(myModel, myTheoremLibrary, 3));
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Developing Antecedent ---"), 1));
        steps.add(new MainProofLevel(m, 3, transformations));

        myAutomatorStack.push(new PushSequence(steps));
    }

    public void prepForUIUpdate() {
        if (SwingUtilities.isEventDispatchThread()) {
            //Changes are happening on the event dispatching thread, which
            //likely means we're in interactive mode--no harm in going ahead
            //and alerting change listeners
            myModel.triggerUIUpdates();
        }
        else {
            //We're on the prover thread, so we're in no danger of a race 
            //condition checking myTakingStepFlag

            //Signal to step() that it shouldn't begin new work until the UI
            //update is complete
            myPrepForUIUpdateFlag = true;

            if (!myTakingStepFlag) {
                //We're not in step, so we're safe to start updating immediately
                myModel.triggerUIUpdates();
            }

            //In the other case, step() will take it from here when it's done
            //with its work
        }
    }

    public void uiUpdateFinished() {
        myPrepForUIUpdateFlag = false;

        if (myWorkerThread != null) {
            myWorkerThread.interrupt();
        }
    }

    public void start() {
        myWorkerThread = Thread.currentThread();

        myRunningFlag = true;
        while (myRunningFlag) {
            step();
        }

        myWorkerThread = null;
    }

    public void pause() {
        myRunningFlag = false;
    }

    public void step() {

        myTakingStepFlag = true;

        if (myPrepForUIUpdateFlag) {
            synchronized (this) {
                myModel.triggerUIUpdates();

                while (myPrepForUIUpdateFlag) {
                    try {
                        wait();
                    }
                    catch (InterruptedException ie) {

                    }
                }
            }
        }

        List<ProofStep> proofSteps = myModel.getProofSteps();

        int originalProofLength = proofSteps.size();
        while (!myAutomatorStack.isEmpty()
                && originalProofLength == proofSteps.size()
                && !myModel.noConsequents()) {

            myAutomatorStack.peek().step(myAutomatorStack, myModel);
        }

        if (myAutomatorStack.isEmpty() || myModel.noConsequents()) {
            myRunningFlag = false;
        }

        myTakingStepFlag = false;
    }
}
