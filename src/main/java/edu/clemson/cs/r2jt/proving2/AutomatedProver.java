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
import java.util.Set;
import java.util.TreeSet;
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

    public boolean doneSearching() {
        return myAutomatorStack.isEmpty();
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
            if (myAutomatorStack.isEmpty()) {
                System.out.println("Proof space exhausted.");
            }
            if (myModel.noConsequents()) {
                System.out.println("Proved.");
            }
            myRunningFlag = false;
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
