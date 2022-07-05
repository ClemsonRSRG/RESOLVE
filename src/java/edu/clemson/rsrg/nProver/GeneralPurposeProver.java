/*
 * GeneralPurposeProver.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver;

import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.FlagDependencies;
import edu.clemson.rsrg.init.output.OutputListener;
import edu.clemson.rsrg.nProver.output.VCProverResult;
import edu.clemson.rsrg.nProver.utilities.Utilities;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.vcgeneration.VCGenerator;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static edu.clemson.rsrg.vcgeneration.VCGenerator.FLAG_VERIFY_VC;

/**
 * <p>
 * This class is a general purpose prover that attempts to prove the {@code VCs} generated by {@link VCGenerator}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Nicodemus Msafiri J. M.
 *
 * @version 1.0
 */
public class GeneralPurposeProver {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>
     * The module scope for the file we are generating automated proofs for.
     * </p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>
     * The number of tries before halting the automated prover
     * </p>
     */
    private final int myNumTriesBeforeHalting;

    /**
     * <p>
     * The various different output listeners that are expecting an update.
     * </p>
     */
    private List<OutputListener> myOutputListeners;

    /**
     * <p>
     * The number of milliseconds before stopping the prove for a VC.
     * </p>
     */
    private final long myTimeout;

    /**
     * <p>
     * The total number of milliseconds spent proving VCs in this file.
     * </p>
     */
    private long myTotalElapsedTime;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * The module's final list of verification conditions.
     * </p>
     */
    private final List<VerificationCondition> myVerificationConditions;

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * String template groups for storing all the prover details.
     * </p>
     */
    private final STGroup mySTGroup;

    /**
     * <p>
     * String template for the prover details model.
     * </p>
     */
    private final ST myVCProofDetailsModel;

    /**
     * <p>
     * A list containing the prover results for each VC.
     * </p>
     */
    private final List<VCProverResult> myVCProverResults;

    // ===========================================================
    // Flag Strings
    // ===========================================================
    public static final String FLAG_SECTION_NAME = "Prover";
    private static final String FLAG_DESC_GP_PROVER = "General Purpose Automated Prover"; // DESC -- Description
    private static final String FLAG_DESC_PROVER_NUMTRIES = "Number of Failed VCs Before Halting the Prover.";
    private static final String[] NUMTRIES_ARGS = { "numtries" };
    private static final String FLAG_DESC_PROVER_TIMEOUT = "Number of Milliseconds to Use as a Timeout Before Skipping Proving a VC.";
    private static final String[] FLAG_TIMEOUT_ARGS = { "milliseconds" };

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * Tells the compiler to prove the sequent VCs
     */
    public static final Flag FLAG_PROVE = new Flag(FLAG_SECTION_NAME, "sprove", FLAG_DESC_GP_PROVER); // sequent prove

    /**
     * <p>
     * Specifies number of milliseconds before skipping proving a VC.
     * </p>
     */
    private static final Flag FLAG_TIMEOUT = new Flag(FLAG_SECTION_NAME, "timeout", FLAG_DESC_PROVER_TIMEOUT,
            FLAG_TIMEOUT_ARGS, Flag.Type.HIDDEN);

    /**
     * <p>
     * Specify number of failed VCs before halting the prover.
     * </p>
     */
    private static final Flag FLAG_NUMTRIES = new Flag(FLAG_SECTION_NAME, "num_tries", FLAG_DESC_PROVER_NUMTRIES,
            NUMTRIES_ARGS, Flag.Type.HIDDEN);

    /**
     * <p>
     * Add all the required and implied flags for the {@code GeneralPurposeProver}.
     * </p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PROVE, FLAG_VERIFY_VC); // adding prove flag, also add verify VC flag
        FlagDependencies.addRequires(FLAG_TIMEOUT, FLAG_PROVE);
        FlagDependencies.addRequires(FLAG_NUMTRIES, FLAG_PROVE);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an instance of the general purpose automated prover.
     * </p>
     *
     * @param vcs
     *            The list of VCs to be proven.
     * @param moduleScope
     *            The module scope associated with {@code name}.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    public GeneralPurposeProver(List<VerificationCondition> vcs, ModuleScope moduleScope,
            CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myCurrentModuleScope = moduleScope;
        myOutputListeners = myCompileEnvironment.getOutputListeners();
        mySTGroup = new STGroupFile("templates/nProverVerboseOutput.stg");
        myTotalElapsedTime = 0;
        myTypeGraph = compileEnvironment.getTypeGraph();
        myVCProverResults = new ArrayList<>(vcs.size());
        myVerificationConditions = vcs;
        myVCProofDetailsModel = mySTGroup.getInstanceOf("outputVCGenDetails");

        // Timeout
        if (myCompileEnvironment.flags.isFlagSet(FLAG_TIMEOUT)) {
            myTimeout = Long.parseLong(myCompileEnvironment.flags.getFlagArgument(FLAG_TIMEOUT, "milliseconds"));
        } else {
            myTimeout = 5000;
        }

        // Number of Tries
        if (myCompileEnvironment.flags.isFlagSet(FLAG_NUMTRIES)) {
            myNumTriesBeforeHalting = Integer
                    .parseInt(myCompileEnvironment.flags.getFlagArgument(FLAG_NUMTRIES, "numtries"));
        } else {
            myNumTriesBeforeHalting = -1;
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the prover setting for how many unproved {@code VCs} we allow before halting.
     * </p>
     *
     * @return The number of tries with -1 indicating that we attempt to prove all VCs.
     */
    public final int getNumTriesBeforeHalting() {
        return myNumTriesBeforeHalting;
    }

    /**
     * <p>
     * This method returns the prover setting for the maximum amount of time we can spend proving each {@code VC}.
     * </p>
     *
     * @return The time that can be spent proving each {@code VC} in ms.
     */
    public final long getTimeout() {
        return myTimeout;
    }

    /**
     * <p>
     * This method returns the total elapsed time that we spent proving {@code VCs} in this {@link ModuleDec}.
     * </p>
     *
     * @return The total elapsed time in ms.
     */
    public final long getTotalElapsedTime() {
        return myTotalElapsedTime;
    }

    /**
     * <p>
     * This method returns a list containing the proof details for each {@code VC}.
     * </p>
     *
     * @return A {@link List} of {@link VCProverResult}.
     */
    public final List<VCProverResult> getVCProverResults() {
        return myVCProverResults;
    }

    /**
     * <p>
     * This method returns the verbose mode output with how we attempted to prove each {@code VCs} in this
     * {@link ModuleDec}.
     * </p>
     *
     * @return A string containing lots of details.
     */
    public final String getVerboseModeOutput() {
        return myVCProofDetailsModel.render();
    }

    /**
     * <p>
     * This method runs the general purpose prover on all the VCs.
     * </p>
     */
    public void proveVCs() {
        // Loop through each of the VCs and attempt to prove them
        for (VerificationCondition vc : myVerificationConditions) {
            // Convert each expression in the VC to a numeric label
            Map<Exp, Integer> labels = Utilities.convertExpToLabel(vc);

            if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                StringBuffer sb = new StringBuffer();
                sb.append("\n================VC ");
                sb.append(vc.getName());
                sb.append("================\n\n");
                for (Exp exp : labels.keySet()) {
                    sb.append(exp.asString(0, 0));
                    sb.append(" -> ");
                    sb.append(labels.get(exp));
                    sb.append("\n");
                }

                myCompileEnvironment.getStatusHandler().info(null, sb.toString());
            }
        }
    }

}