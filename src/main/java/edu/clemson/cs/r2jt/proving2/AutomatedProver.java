/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.automators.AntecedentDeveloper;
import edu.clemson.cs.r2jt.proving2.automators.AntecedentMinimizer;
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
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
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
    
    private final Set<String> myVariableSymbols;

    public AutomatedProver(PerVCProverModel m,
            ImmutableList<Theorem> theoremLibrary, ModuleScope moduleScope) {
        myModel = m;
        myFitnessFunction = new MainProofFitnessFunction(m);

        m.setAutomatedProver(this);

        myTheoremLibrary = theoremLibrary;
        
        //The consequents will contain many symbols like "min_int",
        //"S", "Empty_String", etc.  For the purposes of a number of 
        //optimizations/heuristics it's useful to know which of those come from 
        //programmatic variables and which come from mathematical definitions
        myVariableSymbols = determineVariableSymbols(myModel, moduleScope);
        System.out.println("VARSYM: " + myVariableSymbols);

        PriorityQueue<Transformation> transformationHeap =
                new PriorityQueue<Transformation>(11,
                        new TransformationComparator());
        List<Transformation> theoremTransformations;
        for (Theorem t : theoremLibrary) {
            theoremTransformations = t.getTransformations();

            for (Transformation transformation : theoremTransformations) {
                if (!transformation.couldAffectAntecedent() && 
                        !transformation.introducesQuantifiedVariables()) {
                    transformationHeap.add(transformation);
                }
            }
        }
        List<Transformation> transformations = new LinkedList<Transformation>();
        Transformation top;
        while (!transformationHeap.isEmpty() && 
                myFitnessFunction.calculateFitness(
                    transformationHeap.peek()) >= 0) {
            
            top = transformationHeap.poll();
            transformations.add(top);
            System.out.println(top + " (" + top.getClass() + ") -- "
                    + myFitnessFunction.calculateFitness(top));
        }
        
        if (transformationHeap.size() > 0) {
            System.out.println("<<<<<<<<<<<<<<< recommend against");
            while (!transformationHeap.isEmpty()) {
                top = transformationHeap.poll();
                System.out.println(top + " (" + top.getClass() + ") -- "
                    + myFitnessFunction.calculateFitness(top));
            }
        }
        

        List<Automator> steps = new LinkedList<Automator>();
        steps.add(new VariablePropagator());
        steps.add(new AntecedentMinimizer(myTheoremLibrary));
        steps.add(new VariablePropagator());
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Minimizing Antecedent ---"), 1));
        steps.add(new AntecedentDeveloper(myModel, myVariableSymbols, myTheoremLibrary, 1));
        steps.add(new VariablePropagator());
        steps.add(new AntecedentMinimizer(myTheoremLibrary));
        steps.add(new AntecedentDeveloper(myModel, myVariableSymbols, myTheoremLibrary, 1));
        steps.add(new VariablePropagator());
        steps.add(new AntecedentMinimizer(myTheoremLibrary));
        steps.add(new AntecedentDeveloper(myModel, myVariableSymbols, myTheoremLibrary, 1));
        steps.add(new VariablePropagator());
        steps.add(new AntecedentMinimizer(myTheoremLibrary));
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Developing Antecedent ---"), 1));
        steps.add(new Minimizer(myTheoremLibrary));
        steps.add(new ApplyN(
                new NoOpLabel("--- Done Minimizing Consequent ---"), 1));
        steps.add(Simplify.INSTANCE);
        steps.add(new MainProofLevel(m, 3, transformations));

        myAutomatorStack.push(new PushSequence(steps));
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
        for (PExp consequent : model.getConsequentList()) {
            symbols.addAll(consequent.getSymbolNames());
        }
        
        //Next, find all those that come from mathematical definitions
        Set<String> mathSymbols = new HashSet<String>();
        for (String s : symbols) {
            List<SymbolTableEntry> entries = moduleScope.query(
                    new NameQuery(null, s, ImportStrategy.IMPORT_RECURSIVE, 
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
