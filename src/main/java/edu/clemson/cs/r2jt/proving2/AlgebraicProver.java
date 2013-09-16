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
import edu.clemson.cs.r2jt.proving2.transformations.EliminateTrueConjunctInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceSymmetricEqualityWithTrueInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceTheoremInConsequentWithTrue;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.FlagManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
     * <p> The main prover flag. Causes the integrated prover to attempt to
     * dispatch generated VCs. </p>
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
    private final int myTimeout;

    public AlgebraicProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            final boolean startInteractive, CompileEnvironment environment,
            ProverListener listener) {

        myInstanceEnvironment = environment;
        myModels = new PerVCProverModel[vcs.size()];
        myAutomatedProvers = new AutomatedProver[vcs.size()];
        myModuleScope = scope;

        if (environment.flags.isFlagSet(Prover.FLAG_TIMEOUT)) {
            myTimeout =
                    Integer.parseInt(environment.flags.getFlagArgument(
                    Prover.FLAG_TIMEOUT, Prover.FLAG_TIMEOUT_ARG_NAME));
        } else {
            myTimeout = -1;
        }

        if (listener != null) {
            myProverListeners.add(listener);
        }

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
                new AutomatedProver(myModels[0], myTheoremLibrary, scope,
                myTimeout);

        if (environment.flags.isFlagSet(Prover.FLAG_NOGUI)) {
            myUI = null;
        } else {
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
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            } catch (InvocationTargetException ite) {
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
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out.println("AlgebraicProver - Starting");
            }
            //This will block until it either finishes proving or is told to
            //stop by, e.g., a "pause" action
            if (!myInteractiveModeFlag) {
                myAutomatedProvers[myVCIndex].start();
            }
            if (!FlagManager.getInstance().isFlagSet("nodebug")) {
                System.out.println("AlgebraicProver - Out -- Interactive: "
                        + myInteractiveModeFlag);
            }
            //myModels[myVCIndex].touch();
            if (myModels[myVCIndex].noConsequents()
                    || myAutomatedProvers[myVCIndex].doneSearching()) {
                //We finished searching--either proved or failed

                boolean proved = myModels[myVCIndex].noConsequents();
                for (ProverListener l : myProverListeners) {
                    l.vcResult(proved, myModels[myVCIndex], null);
                }

                if (myVCIndex == myVCs.size() - 1) {
                    //We're done with every VC
                    myRunningFlag = false;

                    // YS: check if we have a ui
                    if (myUI != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                myUI.setInteractiveMode(true);
                            }
                        });
                    }

                    //TODO: It's unclear if it's possible for us to be in
                    //interactive mode here
                    if (!myInteractiveModeFlag) {
                        outputProofFile();
                    }
                } else {
                    //Start on next VC.
                    setVCIndex(myVCIndex + 1);
                }
            } else {

                //This implements a debugging feature where the prover will 
                //automatically pause whenever a label is reached.  To enable
                //this feature, see NoOpLabel.NoOpLabelApplication.apply().
                if (!myModels[myVCIndex].getProofSteps().isEmpty()
                        && myModels[myVCIndex].getLastProofStep() instanceof LabelStep) {

                    myInteractiveModeFlag = true;
                }

                // YS: check if we have a ui
                if (myUI != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            myUI.setInteractiveMode(true);
                        }
                    });
                }

                //Stopped for some other reason.  Might be because interactive
                //mode is now on, in which case we want to cool our heels
                while (myInteractiveModeFlag && myRunningFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }

        myWorkingThread = null;
    }

    private void outputProofFile() throws IOException {
        FileWriter w = new FileWriter(new File(proofFileName()));

        w.write("Proofs for " + myModuleScope.getModuleIdentifier()
                + " generated " + new Date() + "\n\n");

        w.write("=================================== ");
        w.write("Summary");
        w.write(" ===================================\n\n");

        int[] stepCount = new int[myModels.length];
        int[] searchStepCount = new int[myModels.length];

        boolean doneWithAntecedentDevelopment;

        StringBuilder[] buffers = new StringBuilder[myModels.length];
        for (int i = 0; i < myModels.length; i++) {
            doneWithAntecedentDevelopment = false;

            buffers[i] = new StringBuilder();
            buffers[i].append("=================================== ");
            buffers[i].append(myModels[i].getTheoremName());
            buffers[i].append(" ===================================\n\n");

            if (myModels[i].noConsequents()) {
                buffers[i].append("[PROVED] via:\n\n");

                PerVCProverModel workingModel =
                        new PerVCProverModel(myTypeGraph, myVCs.get(i)
                        .getName(), myVCs.get(i), myTheoremLibrary);

                buffers[i].append(workingModel.toString());
                buffers[i].append("\n\n");

                Application lastApplication = null;
                Transformation stepTransformation;
                List<ProofStep> steps = myModels[i].getProductiveProofSteps();
                //List<ProofStep> steps = myModels[i].getProofSteps();
                for (ProofStep step : steps) {
                    workingModel.mimic(step);

                    if (step.getApplication() != lastApplication) {
                        stepCount[i]++;

                        if (doneWithAntecedentDevelopment
                                && !(step.getTransformation() instanceof EliminateTrueConjunctInConsequent)
                                && !(step.getTransformation() instanceof ReplaceSymmetricEqualityWithTrueInConsequent)
                                && !(step.getTransformation() instanceof ReplaceTheoremInConsequentWithTrue)) {
                            searchStepCount[i]++;
                        }

                        lastApplication = step.getApplication();
                        stepTransformation = step.getTransformation();

                        if (stepTransformation instanceof NoOpLabel) {
                            doneWithAntecedentDevelopment =
                                    doneWithAntecedentDevelopment
                                    || stepTransformation
                                    .toString()
                                    .equals(
                                    AutomatedProver.SEARCH_START_LABEL);

                            buffers[i].append(stepTransformation.toString());
                            buffers[i].append("\n\n");
                        } else {
                            buffers[i].append("Applied ");
                            buffers[i].append(stepTransformation);
                            buffers[i].append("\n\n");
                            buffers[i].append(workingModel.toString());
                            buffers[i].append("\n\n");
                        }
                    }
                }

                buffers[i].append("Q.E.D.\n\n");
            } else {
                buffers[i].append("[NOT PROVED]\n\n");
            }

            w.write("\t" + myModels[i].getTheoremName() + "\t......... ");

            if (myModels[i].noConsequents()) {
                w.write("proved in "
                        + myAutomatedProvers[i].getLastStartLength()
                        + "ms via " + stepCount[i] + " steps ("
                        + searchStepCount[i] + " search)\n");
            } else {
                w.write("[SKIPPED] after "
                        + myAutomatedProvers[i].getLastStartLength() + "ms\n");
            }
        }

        w.write("\n");

        for (int i = 0; i < myModels.length; i++) {
            w.write(buffers[i].toString());
        }

        w.flush();
        w.close();
    }

    private void setVCIndex(int index) {
        if (!FlagManager.getInstance().isFlagSet("nodebug")) {
            System.out.println("Algebraic Prover - SET VC INDEX " + index);
        }
        int previousIndex = myVCIndex;

        myVCIndex = index;

        if (myModels[myVCIndex] == null) {
            myModels[myVCIndex] =
                    new PerVCProverModel(myTypeGraph, myVCs.get(myVCIndex)
                    .getName(), myVCs.get(myVCIndex), myTheoremLibrary);
            myAutomatedProvers[myVCIndex] =
                    new AutomatedProver(myModels[myVCIndex], myTheoremLibrary,
                    myModuleScope, myTimeout);
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
            } else {
                try {
                    while (myUI.getModel() != myModels[myVCIndex]) {
                        try {
                            SwingUtilities.invokeAndWait(setModel);
                        } catch (InterruptedException ie) {
                        }
                    }
                } catch (InvocationTargetException ite) {
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
