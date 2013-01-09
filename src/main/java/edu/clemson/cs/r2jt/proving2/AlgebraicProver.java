/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.gui.JProverFrame;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.justifications.Library;
import edu.clemson.cs.r2jt.mathtype.TheoremEntry;
import edu.clemson.cs.r2jt.mathtype.EntryTypeQuery;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.mathtype.ModuleScope;
import edu.clemson.cs.r2jt.mathtype.ScopeRepository;
import edu.clemson.cs.r2jt.mathtype.SymbolTable;
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
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;

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

    private final JProverFrame myUI;

    private boolean myInteractiveModeFlag = false;

    private boolean myRunningFlag = false;

    private Thread myWorkingThread;

    public AlgebraicProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            boolean startInteractive) {

        myModels = new PerVCProverModel[vcs.size()];
        myAutomatedProvers = new AutomatedProver[vcs.size()];

        List<TheoremEntry> theoremEntries =
                scope.query(new EntryTypeQuery(TheoremEntry.class,
                        ImportStrategy.IMPORT_RECURSIVE,
                        FacilityStrategy.FACILITY_IGNORE));

        List<Theorem> theorems = new LinkedList<Theorem>();
        for (TheoremEntry e : theoremEntries) {
            theorems.add(new Theorem(e.getAssertion(), new Library(e)));
        }

        myTheoremLibrary = new ArrayBackedImmutableList<Theorem>(theorems);

        myModels[0] =
                new PerVCProverModel(g, vcs.get(0).getName(), vcs.get(0),
                        myTheoremLibrary);
        myAutomatedProvers[0] =
                new AutomatedProver(myModels[0], myTheoremLibrary);

        JProverFrame proverPanel = new JProverFrame(myModels[0]);
        proverPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        proverPanel.setVisible(true);
        proverPanel.setInteractiveMode(startInteractive);

        proverPanel.addNextVCButtonActionListener(NEXT_VC);
        proverPanel.addLastVCButtonActionListener(LAST_VC);
        proverPanel.addPlayButtonActionListener(GO_AUTOMATIC);
        proverPanel.addPauseButtonActionListener(GO_INTERACTIVE);
        proverPanel.addStepButtonActionListener(STEP_PROVER);

        myTypeGraph = g;
        myVCs = vcs;
        myUI = proverPanel;

        myInteractiveModeFlag = startInteractive;
    }

    public synchronized void start() {
        myWorkingThread = Thread.currentThread();
        myRunningFlag = true;
        while (myRunningFlag) {
            System.out.println("Top");
            //This will block until it either finishes proving or is told to
            //stop by, e.g., a "pause" action
            if (!myInteractiveModeFlag) {
                myAutomatedProvers[myVCIndex].start();
            }
            System.out.println("Out -- Interactive: " + myInteractiveModeFlag);
            myModels[myVCIndex].touch();
            if (myModels[myVCIndex].noConsequents()) {
                //Proved.

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
        System.out.println("SET VC INDEX " + index);
        int previousIndex = myVCIndex;

        myVCIndex = index;

        if (myModels[myVCIndex] == null) {
            myModels[myVCIndex] =
                    new PerVCProverModel(myTypeGraph, myVCs.get(myVCIndex)
                            .getName(), myVCs.get(myVCIndex), myTheoremLibrary);
            myAutomatedProvers[myVCIndex] =
                    new AutomatedProver(myModels[myVCIndex], myTheoremLibrary);
        }

        if (myUI != null) {
            myUI.setModel(myModels[myVCIndex]);
        }

        if (!myInteractiveModeFlag) {
            myAutomatedProvers[previousIndex].pause();
            //The prover thread will take care of starting the appropriate
            //automated prover now
        }
    }

    private class GoInteractive implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            myInteractiveModeFlag = true;
            myUI.setInteractiveMode(true);
            myAutomatedProvers[myVCIndex].pause();
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
}
