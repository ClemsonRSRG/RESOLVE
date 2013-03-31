/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.gui.JProverFrame;
import edu.clemson.cs.r2jt.proving2.justifications.Library;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.LabelStep;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private final CompileEnvironment myInstanceEnvironment;

    public AlgebraicProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            final boolean startInteractive, CompileEnvironment environment) {

        myInstanceEnvironment = environment;
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

    public synchronized void start() throws IOException {
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

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            myUI.setInteractiveMode(true);
                        }
                    });

                    //TODO: It's unclear if it's possible for us to be in
                    //interactive mode here
                    if (!myInteractiveModeFlag) {
                        outputProofFile();
                    }
                }
                else {
                    //Start on next VC.
                    setVCIndex(myVCIndex + 1);
                }
            }
            else {

                //This implements a debugging feature where the prover will 
                //automatically pause whenever a label is reached.  To enable
                //this feature, see NoOpLabel.NoOpLabelApplication.apply().
                if (!myModels[myVCIndex].getProofSteps().isEmpty()
                        && myModels[myVCIndex].getLastProofStep() instanceof LabelStep) {

                    myInteractiveModeFlag = true;
                }

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        myUI.setInteractiveMode(true);
                    }
                });

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

    private void outputProofFile() throws IOException {
        FileWriter w = new FileWriter(new File(proofFileName()));

        for (int i = 0; i < myModels.length; i++) {
            w.write("=================================== "
                    + myModels[i].getTheoremName()
                    + " ===================================\n\n");

            if (myModels[i].getConsequentList().isEmpty()) {
                w.write("[PROVED] via:\n\n");

                PerVCProverModel workingModel =
                        new PerVCProverModel(myTypeGraph, myVCs.get(i)
                                .getName(), myVCs.get(i), myTheoremLibrary);

                w.write(workingModel.toString() + "\n\n");

                Application lastApplication = null;
                Transformation stepTransformation;
                List<ProofStep> steps = myModels[i].getProductiveProofSteps();
                //List<ProofStep> steps = myModels[i].getProofSteps();
                for (ProofStep step : steps) {
                    workingModel.mimic(step);

                    if (step.getApplication() != lastApplication) {
                        lastApplication = step.getApplication();
                        stepTransformation = step.getTransformation();

                        if (stepTransformation instanceof NoOpLabel) {
                            w.write("" + stepTransformation + "\n\n");
                        }
                        else {
                            w.write("Applied " + stepTransformation + "\n\n");
                            w.write(workingModel.toString() + "\n\n");
                        }
                    }
                }

                w.write("Q.E.D.\n\n");
            }
            else {
                w.write("[NOT PROVED]\n\n");
            }
        }

        w.flush();
        w.close();
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

            if (SwingUtilities.isEventDispatchThread()) {
                setModel.run();
            }
            else {
                try {
                    while (myUI.getModel() != myModels[myVCIndex]) {
                        try {
                            SwingUtilities.invokeAndWait(setModel);
                        }
                        catch (InterruptedException ie) {

                        }
                    }
                }
                catch (InvocationTargetException ite) {
                    throw new RuntimeException(ite);
                }
            }
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

    private String proofFileName() {
        File file = myInstanceEnvironment.getTargetFile();
        ModuleID cid = myInstanceEnvironment.getModuleID(file);
        file = myInstanceEnvironment.getFile(cid);
        String filename = file.toString();
        int temp = filename.indexOf(".");
        String tempfile = filename.substring(0, temp);
        String mainFileName;

        mainFileName = tempfile + ".proof";

        return mainFileName;
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
            System.out.println("AlgebraicProver - step");
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
