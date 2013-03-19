/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving2.gui.JProverFrame;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.justifications.Library;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.verification.Verifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author hamptos
 */
public class AlgebraicProver {

    private static final String FLAG_DESC_NEW_PROVE =
            "Verify target file with RESOLVE's integrated prover.";

    private static final String FLAG_DESC_INTERACTIVE =
            "Start the prover in interactive mode.";

    /**
     * <p>
     * The main prover flag. Causes the integrated prover to attempt to dispatch
     * generated VCs.
     * </p>
     */
    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "newprove", FLAG_DESC_NEW_PROVE);

    /**
     * <p>Makes the prover start in interactive mode by default.</p>
     */
    public static final Flag FLAG_INTERACTIVE =
            new Flag(Prover.FLAG_SECTION_NAME, "interactive",
                    FLAG_DESC_INTERACTIVE);

    public static void setUpFlags() {
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);

        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
    }

    private final NextVC NEXT_VC = new NextVC();
    private final LastVC LAST_VC = new LastVC();
    private final StepProver STEP_PROVER = new StepProver();
    private final GoInteractive GO_INTERACTIVE = new GoInteractive();
    private final GoAutomatic GO_AUTOMATIC = new GoAutomatic();

    private int myVCIndex;

    private final PerVCProverModel[] myModels;
    private final AutomatedProver[] myAutomatedProvers;

    private final List<VC> myVCs;
    private final TypeGraph myTypeGraph;
    private final ImmutableList<Theorem> myTheoremLibrary;

    private JProverFrame myUI;

    private boolean myInteractiveModeFlag = false;

    private boolean myRunningFlag = false;

    private Thread myWorkingThread;

    private final List<ProverListener> myProverListeners =
            new LinkedList<ProverListener>();

    private final ModuleScope myModuleScope;

    public AlgebraicProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            final boolean startInteractive, CompileEnvironment environment) {

        myModels = new PerVCProverModel[vcs.size()];
        myAutomatedProvers = new AutomatedProver[vcs.size()];
        myModuleScope = scope;

        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        ImportStrategy.IMPORT_RECURSIVE,
                        FacilityStrategy.FACILITY_IGNORE));

        //Ensure that the theorems are in a consistent (even if arbitrary) order
        //so that proof results are likewise consistent
        Collections.sort(theoremEntries, new AlphabeticalByTheoremName());

        List<Theorem> theorems = new LinkedList<Theorem>();
        for (TheoremEntry e : theoremEntries) {
            theorems.add(new Theorem(e.getAssertion(), new Library(e)));
        }

        myTheoremLibrary = new ArrayBackedImmutableList<Theorem>(theorems);

        myModels[0] =
                new PerVCProverModel(g, vcs.get(0).getName(), vcs.get(0),
                        myTheoremLibrary);
        myAutomatedProvers[0] =
                new AutomatedProver(myModels[0], myTheoremLibrary, scope);

        if (environment.flags.isFlagSet(Prover.FLAG_NOGUI)) {
            myUI = null;
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        JProverFrame proverPanel =
                                new JProverFrame(myModels[0]);
                        proverPanel
                                .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        proverPanel.setVisible(true);
                        proverPanel.setInteractiveMode(startInteractive);

                        proverPanel.addNextVCButtonActionListener(NEXT_VC);
                        proverPanel.addLastVCButtonActionListener(LAST_VC);
                        proverPanel.addPlayButtonActionListener(GO_AUTOMATIC);
                        proverPanel
                                .addPauseButtonActionListener(GO_INTERACTIVE);
                        proverPanel.addStepButtonActionListener(STEP_PROVER);

                        proverPanel.setInteractiveMode(startInteractive);
                        myUI = proverPanel;
                    }
                });
            }
            catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
            catch (InvocationTargetException ite) {
                throw new RuntimeException(ite);
            }
        }

        myTypeGraph = g;
        myVCs = vcs;

        myInteractiveModeFlag = startInteractive;
    }

    public void addProverListener(ProverListener l) {
        myProverListeners.add(l);
    }

    public void removeProverListener(ProverListener l) {
        myProverListeners.remove(l);
    }

    public synchronized void start() {
        myWorkingThread = Thread.currentThread();
        myRunningFlag = true;
        while (myRunningFlag) {
            System.out.println("AlgebraicProver - Starting");
            //This will block until it either finishes proving or is told to
            //stop by, e.g., a "pause" action
            if (!myInteractiveModeFlag) {
                myAutomatedProvers[myVCIndex].start();
            }
            System.out.println("AlgebraicProver - Out -- Interactive: "
                    + myInteractiveModeFlag);
            //myModels[myVCIndex].touch();
            if (myModels[myVCIndex].noConsequents()
                    || myAutomatedProvers[myVCIndex].doneSearching()) {
                //We finished searching--either proved or failed

                if (myVCIndex == myVCs.size() - 1) {
                    //We're done with every VC
                    myRunningFlag = false;
                }
                else {
                    //Start on next VC.
                    setVCIndex(myVCIndex + 1);
                }
            }
            else {
                //Stopped for some other reason.  Might be because interactive
                //mode is now on, in which case we want to cool our heels
                while (myInteractiveModeFlag && myRunningFlag) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ie) {

                    }
                }
            }
        }

        myWorkingThread = null;
    }

    private void setVCIndex(int index) {
        System.out.println("Algebraic Prover - SET VC INDEX " + index);
        int previousIndex = myVCIndex;

        myVCIndex = index;

        if (myModels[myVCIndex] == null) {
            myModels[myVCIndex] =
                    new PerVCProverModel(myTypeGraph, myVCs.get(myVCIndex)
                            .getName(), myVCs.get(myVCIndex), myTheoremLibrary);
            myAutomatedProvers[myVCIndex] =
                    new AutomatedProver(myModels[myVCIndex], myTheoremLibrary,
                            myModuleScope);
        }

        if (myUI != null) {
            Runnable setModel = new Runnable() {

                @Override
                public void run() {
                    myUI.setModel(myModels[myVCIndex]);
                }
            };

            invokeAndWait(setModel);
        }

        if (!myInteractiveModeFlag) {
            myAutomatedProvers[previousIndex].pause();
            //The prover thread will take care of starting the appropriate
            //automated prover now
        }
    }

    private void invokeAndWait(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(r);
            }
            catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
            catch (InvocationTargetException ite) {
                throw new RuntimeException(ite);
            }
        }
    }

    private class GoInteractive implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            myInteractiveModeFlag = true;
            myAutomatedProvers[myVCIndex].pause();
            myUI.setInteractiveMode(true);
        }
    }

    private class GoAutomatic implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            myInteractiveModeFlag = false;
            myUI.setInteractiveMode(false);
            myWorkingThread.interrupt();
        }
    }

    private class StepProver implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            myAutomatedProvers[myVCIndex].step();
        }
    }

    private class NextVC implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (myVCIndex < myModels.length - 1) {
                setVCIndex(myVCIndex + 1);
            }
        }
    }

    private class LastVC implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (myVCIndex > 0) {
                setVCIndex(myVCIndex - 1);
            }
        }
    }

    private class AlphabeticalByTheoremName implements Comparator<TheoremEntry> {

        @Override
        public int compare(TheoremEntry o1, TheoremEntry o2) {
            return o1.getName().compareTo(o2.getName());
        }

    }
}
