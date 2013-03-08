/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.automators.AntecedentDeveloper;
import edu.clemson.cs.r2jt.proving2.automators.ApplyN;
import edu.clemson.cs.r2jt.proving2.automators.Automator;
import edu.clemson.cs.r2jt.proving2.automators.MainProofLevel;
import edu.clemson.cs.r2jt.proving2.automators.Minimizer;
import edu.clemson.cs.r2jt.proving2.automators.PushSequence;
import edu.clemson.cs.r2jt.proving2.automators.Simplify;
import edu.clemson.cs.r2jt.proving2.automators.VariablePropagator;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
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

    private final Object TRANSPORT_LOCK = new Object();
    private final Object WORKER_THREAD_LOCK = new Object();
    private Thread myWorkerThread;

    private MainProofFitnessFunction myFitnessFunction;

    public AutomatedProver(PerVCProverModel m,
            ImmutableList<Theorem> theoremLibrary) {
        myModel = m;
        myFitnessFunction = new MainProofFitnessFunction(m);

        m.setAutomatedProver(this);

        myTheoremLibrary = theoremLibrary;

        List<PExp> truths = new LinkedList<PExp>();
        for (Theorem t : theoremLibrary) {
            truths.add(t.getAssertion());
        }

        PriorityQueue<Transformation> transformationHeap =
                new PriorityQueue<Transformation>(11,
                        new TransformationComparator());
        List<Transformation> theoremTransformations;
        for (Theorem t : theoremLibrary) {
            theoremTransformations = t.getTransformations();

            for (Transformation transformation : theoremTransformations) {
                if (!transformation.couldAffectAntecedent()) {
                    transformationHeap.add(transformation);
                }
            }
        }
        List<Transformation> transformations = new LinkedList<Transformation>();
        Transformation top;
        while (!transformationHeap.isEmpty()) {
            top = transformationHeap.poll();
            transformations.add(top);
            System.out.println(top + " (" + top.getClass() + ") -- "
                    + myFitnessFunction.calculateFitness(top));
        }

        List<Automator> steps = new LinkedList<Automator>();
        steps.add(new VariablePropagator());
        steps.add(new AntecedentDeveloper(myModel, myTheoremLibrary, 1));
        steps.add(new VariablePropagator());
        steps.add(new AntecedentDeveloper(myModel, myTheoremLibrary, 1));
        steps.add(new VariablePropagator());
        steps.add(new AntecedentDeveloper(myModel, myTheoremLibrary, 1));
        steps.add(new VariablePropagator());
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Developing Antecedent ---"), 1));
        steps.add(new Minimizer(myTheoremLibrary));
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Minimizing Consequent ---"), 1));
        steps.add(Simplify.INSTANCE);
        steps.add(new MainProofLevel(m, 3, transformations));

        myAutomatorStack.push(new PushSequence(steps));
    }

    public boolean isRunning() {
        return myRunningFlag;
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

        System.out.println("AutomatedProver - uiUpdateFinished");

        if (myWorkerThread != null) {
            System.out.println("AutomatedProver - Interrupting");
            myWorkerThread.interrupt();
        }
    }

    public boolean doneSearching() {
        return myAutomatorStack.isEmpty();
    }

    public void start() {

        //This synchronization provides a convenient way for other methods to
        //wait until we've actually gotten out of the automated proof loop--
        //just synchronize on TRANSPORT_LOCK
        synchronized (TRANSPORT_LOCK) {
            if (myWorkerThread != null) {
                throw new RuntimeException("Can't start from two threads.");
            }

            myWorkerThread = Thread.currentThread();

            System.out
                    .println("============= AutomatedProver - start() ==============");

            myRunningFlag = true;
            while (myRunningFlag) {
                step();
            }

            System.out.println("AutomatedProver - end of start()");

            synchronized (WORKER_THREAD_LOCK) {
                myWorkerThread = null;
            }
        }
    }

    public void pause() {
        System.out.println("AutomatedProver - pause()");
        myRunningFlag = false;
        myPrepForUIUpdateFlag = false;

        synchronized (WORKER_THREAD_LOCK) {
            if (myWorkerThread != null) {
                myWorkerThread.interrupt();
            }
        }

        synchronized (TRANSPORT_LOCK) {
            //Redundant, just suppressing empty block warning.  This 
            //synchronization just serves to make us wait here while the prover
            //loop unwinds and start() terminates
            myPrepForUIUpdateFlag = false;
        }

        System.out.println("AutomatedProver - end of pause()");
    }

    public void step() {

        myTakingStepFlag = true;

        if (myPrepForUIUpdateFlag) {
            System.out.println("AutomatedProver - Prepping for UI update");

            //We're not actually trying to make this code mutually exclusive
            //with anything--we're just grabbing the object's monitor so we can
            //wait()
            synchronized (this) {
                if (myRunningFlag) {
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
            System.out.println("AutomatedProver - Done with UI update");
        }

        if (myRunningFlag) {
            List<ProofStep> proofSteps = myModel.getProofSteps();

            int originalProofLength = proofSteps.size();
            while (!myAutomatorStack.isEmpty()
                    && originalProofLength == proofSteps.size()
                    && !myModel.noConsequents()) {

                myAutomatorStack.peek().step(myAutomatorStack, myModel);
            }

            if (myAutomatorStack.isEmpty() || myModel.noConsequents()) {
                if (myAutomatorStack.isEmpty()) {
                    System.out.println("Proof space exhausted.");
                }
                if (myModel.noConsequents()) {
                    System.out.println("Proved.");
                }
                myRunningFlag = false;
            }
        }

        myTakingStepFlag = false;
    }

    private class TransformationComparator
            implements
                Comparator<Transformation> {

        @Override
        public int compare(Transformation o1, Transformation o2) {
            int result;

            double fitness1 = myFitnessFunction.calculateFitness(o1);
            double fitness2 = myFitnessFunction.calculateFitness(o2);

            if (fitness1 > fitness2) {
                result = -1;
            }
            else if (fitness2 > fitness1) {
                result = 1;
            }
            else {
                result = 0;
            }

            return result;
        }
    }
}
