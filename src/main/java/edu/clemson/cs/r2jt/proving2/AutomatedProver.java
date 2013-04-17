/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.automators.AntecedentDeveloper;
import edu.clemson.cs.r2jt.proving2.automators.AntecedentMinimizer;
import edu.clemson.cs.r2jt.proving2.automators.ApplyN;
import edu.clemson.cs.r2jt.proving2.automators.Automator;
import edu.clemson.cs.r2jt.proving2.automators.EliminateObviousAntecedents;
import edu.clemson.cs.r2jt.proving2.automators.EliminateRedundantAntecedents;
import edu.clemson.cs.r2jt.proving2.automators.MainProofLevel;
import edu.clemson.cs.r2jt.proving2.automators.Minimizer;
import edu.clemson.cs.r2jt.proving2.automators.PushSequence;
import edu.clemson.cs.r2jt.proving2.automators.Simplify;
import edu.clemson.cs.r2jt.proving2.automators.VariablePropagator;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 *
 * @author hamptos
 */
public class AutomatedProver {

    /* The flags in this section permit certain heuristics to be turned on/off
     * for testing purposes.  They should all default to "on".
     */

    //Look for statements of identity functions (like i + 0 = i) and never 
    //use them to expand something.  I.e., never take i and make it i + 0.
    public static final boolean H_DETECT_IDENTITY_EXPANSION = true;

    //Prevent new givens from being developed if they don't mention "important"
    //terms that appear in the consequent
    public static final boolean H_ONLY_DEVELOP_RELEVANT_TERMS = true;

    //Only accept antecedent developments that put things in new terms
    public static final boolean H_ENCOURAGE_ANTECEDENT_DIVERSITY = true;

    //Apply minimization steps to antecedents and consequents
    public static final boolean H_PERFORM_MINIMIZATION = true;

    //Detect and avoid cycles
    public static final boolean H_DETECT_CYCLES = true;

    //Use a fitness function to try and order transformations so that "better"
    //transformations are applied first
    public static final boolean H_BEST_FIRST_CONSEQUENT_EXPLORATION = true;

    public static final String SEARCH_START_LABEL =
            "--- Done Minimizing Consequent ---";

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

    private MainProofFitnessFunction myMainProofFitnessFunction;
    private AntecedentDeveloperFitnessFunction myAntecedentDeveloperFitnessFunction;

    private final Set<String> myVariableSymbols;

    private final int myTimeout;

    private long myStartTime;
    private long myEndTime;

    public AutomatedProver(PerVCProverModel m,
            ImmutableList<Theorem> theoremLibrary, ModuleScope moduleScope,
            int timeout) {
        myModel = m;
        myMainProofFitnessFunction = new MainProofFitnessFunction(m);
        myAntecedentDeveloperFitnessFunction =
                new AntecedentDeveloperFitnessFunction(m);
        myTimeout = timeout;

        //This looks weird but suppresses a "leaked this" warning
        AutomatedProver p = this;
        m.setAutomatedProver(p);

        myTheoremLibrary = theoremLibrary;

        //The consequents will contain many symbols like "min_int",
        //"S", "Empty_String", etc.  For the purposes of a number of 
        //optimizations/heuristics it's useful to know which of those come from 
        //programmatic variables and which come from mathematical definitions
        myVariableSymbols = determineVariableSymbols(myModel, moduleScope);
        System.out.println("VARSYM: " + myVariableSymbols);

        System.out.println("###################### consequent transformations");
        List<Transformation> consequentTransformations =
                orderByFitnessFunction(myTheoremLibrary,
                        myMainProofFitnessFunction);

        System.out.println("###################### antecedent transformations");
        List<Transformation> antecedentTransformations =
                orderByFitnessFunction(myTheoremLibrary,
                        myAntecedentDeveloperFitnessFunction);

        List<Automator> steps = new LinkedList<Automator>();
        steps.add(new VariablePropagator());

        if (H_PERFORM_MINIMIZATION) {
            steps.add(new AntecedentMinimizer(myTheoremLibrary));
        }

        steps.add(new VariablePropagator());
        steps.add(new EliminateObviousAntecedents());
        steps.add(new ApplyN(new NoOpLabel(this,
                "--- Done Minimizing Antecedent ---"), 1));

        for (int i = 0; i < 3; i++) {
            steps.add(new AntecedentDeveloper(myModel, myVariableSymbols,
                    antecedentTransformations, 1));
            steps.add(new VariablePropagator());

            if (H_PERFORM_MINIMIZATION) {
                steps.add(new AntecedentMinimizer(myTheoremLibrary));
            }

            steps.add(EliminateRedundantAntecedents.INSTANCE);
            steps.add(new EliminateObviousAntecedents());
        }
        steps.add(new ApplyN(new NoOpLabel(this,
                "--- Done Developing Antecedent ---"), 1));

        if (H_PERFORM_MINIMIZATION) {
            steps.add(new Minimizer(myTheoremLibrary));
        }

        steps.add(new ApplyN(new NoOpLabel(this, SEARCH_START_LABEL), 1));

        steps.add(Simplify.INSTANCE);
        steps.add(new MainProofLevel(m, 3, consequentTransformations));

        myAutomatorStack.push(new PushSequence(steps));
    }

    private List<Transformation> orderByFitnessFunction(
            Iterable<Theorem> theorems, FitnessFunction<Transformation> f) {

        PriorityQueue<Transformation> transformationHeap =
                new PriorityQueue<Transformation>(11,
                        new TransformationComparator(f));
        List<Transformation> theoremTransformations;
        for (Theorem t : theorems) {
            theoremTransformations = t.getTransformations();

            for (Transformation transformation : theoremTransformations) {
                transformationHeap.add(transformation);
            }
        }

        List<Transformation> transformations = new LinkedList<Transformation>();
        Transformation top;
        while (!transformationHeap.isEmpty()
                && f.calculateFitness(transformationHeap.peek()) >= 0) {

            top = transformationHeap.poll();
            transformations.add(top);
            System.out.println(top + " (" + top.getClass() + ") -- "
                    + f.calculateFitness(top));
        }

        if (transformationHeap.size() > 0) {
            System.out.println("<<<<<<<<<<<<<<< recommend against");
            while (!transformationHeap.isEmpty()) {
                top = transformationHeap.poll();
                System.out.println(top + " (" + top.getClass() + ") -- "
                        + f.calculateFitness(top));
            }
        }

        return transformations;
    }

    private Set<String> determineVariableSymbols(PerVCProverModel model,
            ModuleScope moduleScope) {

        //With apologies to whoever has to deal with this mess, this is not a
        //very good way of dealing with this.  Ideally the populator would 
        //attach the entry that corresponds to each symbol to its Exp, making
        //answering this questions really easy.  But because the Verifier builds
        //lots of Exps from scratch based on existing ones, getting new 
        //information through the verifier is a nightmare.  Once a better 
        //verifier exists, that change would be a lot easier and this can be
        //made a lot more robust.

        //First, get a list of all symbols
        Set<String> symbols = new HashSet<String>();
        for (Conjunct consequent : model.getConsequentList()) {
            symbols.addAll(consequent.getExpression().getSymbolNames());
        }

        //We also include any symbols that could be easily 'swapped in' by a
        //local theorem equality.  For example, if we know (P o Q) = (P' o Q'),
        //then even if P' and Q' don't currently appear in the consequent... if
        //(P o Q) -does- appear, we'll include P' and Q' as symbols
        for (LocalTheorem t : model.getLocalTheoremList()) {
            if (t.getAssertion().isEquality()) {
                for (Transformation trans : t.getTransformations()) {
                    if (trans instanceof SubstituteInPlaceInConsequent) {
                        Iterator<Application> applications =
                                trans.getApplications(model);

                        if (applications.hasNext()) {
                            symbols.addAll(trans.getReplacementSymbolNames());
                        }
                    }
                }
            }
        }

        //Next, find all those that come from mathematical definitions
        Set<String> mathSymbols = new HashSet<String>();
        for (String s : symbols) {
            List<SymbolTableEntry> entries =
                    moduleScope.query(new NameQuery(null, s,
                            ImportStrategy.IMPORT_RECURSIVE,
                            FacilityStrategy.FACILITY_INSTANTIATE, false));

            if (entries.isEmpty()) {
                //Symbol must be inside an operation scope, in which case it's
                //a programmatic variable
            }
            else {
                boolean math = true;

                Iterator<SymbolTableEntry> entriesIter = entries.iterator();
                SymbolTableEntry entry;
                while (math && entriesIter.hasNext()) {
                    entry = entriesIter.next();
                    math = entry instanceof MathSymbolEntry;
                }

                if (math) {
                    mathSymbols.add(s);
                }
            }
        }

        //Everything that isn't a math symbol is a variable symbol
        symbols.removeAll(mathSymbols);

        return Collections.unmodifiableSet(symbols);
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
            myStartTime = System.currentTimeMillis();

            if (myWorkerThread != null) {
                throw new RuntimeException("Can't start from two threads.");
            }

            myWorkerThread = Thread.currentThread();

            System.out
                    .println("============= AutomatedProver - start() ==============");

            long stopTime = System.currentTimeMillis() + myTimeout;
            myRunningFlag = true;
            while (myRunningFlag
                    && (myTimeout == -1 || System.currentTimeMillis() < stopTime)) {
                workerStep();
            }

            if (myRunningFlag) {
                myAutomatorStack.clear();
            }
            myRunningFlag = false;
            myEndTime = System.currentTimeMillis();

            System.out.println("AutomatedProver - end of start()");

            synchronized (WORKER_THREAD_LOCK) {
                myWorkerThread = null;
            }
        }
    }

    public long getLastStartLength() {
        return myEndTime - myStartTime;
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

    /**
     * <p>markToPause is like {@link #pause() pause()} except that it does not 
     * block and must be called from the worker thread.  This is mostly useful 
     * for proof steps that want to trigger a pause (and can't use pause because
     * they're already on the worker thread).</p>
     */
    public void markToPause() {
        myRunningFlag = false;
        myPrepForUIUpdateFlag = false;
    }

    private void workerStep() {
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
            step();
        }

        myTakingStepFlag = false;
    }

    public void step() {
        myTakingStepFlag = true;

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

        private final FitnessFunction<Transformation> myFitnessFunction;

        public TransformationComparator(FitnessFunction<Transformation> f) {
            myFitnessFunction = f;
        }

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
                //Give us a consistent, if arbitrary, order
                result = o1.getKey().compareTo(o2.getKey());
            }

            return result;
        }
    }
}
