/*
 * CongruenceClassProver.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover;

import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.List;
import static edu.clemson.cs.rsrg.vcgeneration.VCGenerator.FLAG_VERIFY_VC;

/**
 * <p>This class is an {@code Congruence Closure} automated prover
 * that verifies {@code VCs}.</p>
 *
 * @author Mike Kabbani
 * @version 2.0
 */
public class CongruenceClassProver {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>The module scope for the file we are generating automated proofs for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "Prover";
    private static final String FLAG_DESC_CC_PROVER =
            "Congruence Closure Based Prover";
    private static final String FLAG_DESC_PROVER_TIMEOUT =
            "Number of Failed VCs Before Halting the Prover.";
    private static final String[] NUMTRIES_ARGS = { "numtries" };

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>Tells the compiler to prove VCs.</p>
     */
    public static final Flag FLAG_PROVE =
            new Flag(FLAG_SECTION_NAME, "ccprove", FLAG_DESC_CC_PROVER);

    /**
     * <p>Specify number of failed VCs before halting the prover.</p>
     */
    private static final Flag FLAG_NUMTRIES =
            new Flag("Proving", "num_tries", FLAG_DESC_PROVER_TIMEOUT,
                    NUMTRIES_ARGS, Flag.Type.HIDDEN);

    /**
     * <p>Add all the required and implied flags for the {@code CongruenceClassProver}.</p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PROVE, FLAG_VERIFY_VC);
        FlagDependencies.addRequires(FLAG_NUMTRIES, FLAG_PROVE);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an instance of the {@code Congruence Closure}
     * automated prover.</p>
     *
     * @param vcs The list of VCs to be proven.
     * @param moduleScope The module scope associated with {@code name}.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    public CongruenceClassProver(List<ImmutableVC> vcs,
            ModuleScope moduleScope, CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myCurrentModuleScope = moduleScope;
        myTypeGraph = compileEnvironment.getTypeGraph();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

}