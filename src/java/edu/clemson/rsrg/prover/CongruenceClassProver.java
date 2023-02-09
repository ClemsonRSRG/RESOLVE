/*
 * CongruenceClassProver.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover;

import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.FlagDependencies;
import edu.clemson.rsrg.init.output.OutputListener;
import edu.clemson.rsrg.prover.output.PerVCProverModel;
import edu.clemson.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.List;
import static edu.clemson.rsrg.vcgeneration.VCGenerator.FLAG_VERIFY_VC;

/**
 * <p>
 * This class is an {@code Congruence Closure} automated prover that verifies {@code VCs}.
 * </p>
 *
 * @author Mike Kabbani
 *
 * @version 2.0
 */
public class CongruenceClassProver {

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
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * The array of VC models.
     * </p>
     */
    private final PerVCProverModel[] myVCModels;

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "Prover";
    private static final String FLAG_DESC_CC_PROVER = "Congruence Closure Based Prover";
    private static final String FLAG_DESC_PROVER_NUMTRIES = "Number of Failed VCs Before Halting the Prover.";
    private static final String[] NUMTRIES_ARGS = { "numtries" };
    private static final String FLAG_DESC_PROVER_TIMEOUT = "Number of Milliseconds to Use as a Timeout Before Skipping Proving a VC.";
    private static final String[] FLAG_TIMEOUT_ARGS = { "milliseconds" };

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * Tells the compiler to prove VCs.
     * </p>
     */
    public static final Flag FLAG_PROVE = new Flag(FLAG_SECTION_NAME, "ccprove", FLAG_DESC_CC_PROVER);

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
    private static final Flag FLAG_NUMTRIES = new Flag("Proving", "num_tries", FLAG_DESC_PROVER_NUMTRIES, NUMTRIES_ARGS,
            Flag.Type.HIDDEN);

    /**
     * <p>
     * Add all the required and implied flags for the {@code CongruenceClassProver}.
     * </p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PROVE, FLAG_VERIFY_VC);
        FlagDependencies.addRequires(FLAG_TIMEOUT, FLAG_PROVE);
        FlagDependencies.addRequires(FLAG_NUMTRIES, FLAG_PROVE);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an instance of the {@code Congruence Closure} automated prover.
     * </p>
     *
     * @param vcs
     *            The list of VCs to be proven.
     * @param moduleScope
     *            The module scope associated with {@code name}.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    public CongruenceClassProver(List<ImmutableVC> vcs, ModuleScope moduleScope,
            CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myCurrentModuleScope = moduleScope;
        myOutputListeners = myCompileEnvironment.getOutputListeners();
        myTypeGraph = compileEnvironment.getTypeGraph();
        myVCModels = new PerVCProverModel[vcs.size()];

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

    // ===========================================================
    // Private Methods
    // ===========================================================

}
