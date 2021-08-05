/*
 * GeneralPurposeProver.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.nProver;

import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.init.output.OutputListener;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;

import java.util.List;

import static edu.clemson.cs.rsrg.vcgeneration.VCGenerator.FLAG_VERIFY_VC;

public class GeneralPurposeProver {

    //member fields

    //private final CompileEnvironment myCompileEnvironment;
    //private final ModuleScope myCurrentModuleScope;
    //private final int myNumVCsBeforeHalting;
    //private final long myTimeout;
    //private List<OutputListener> myOutputListeners;
    //private final TypeGraph myTypeGraph;

    //flag strings
    public static final String FLAG_SECTION_NAME = "Prover";
    private static final String FLAG_DESC_GP_PROVER =
            "General Purpose Automated Prover"; // DESC -- Description
    private static final String FLAG_DESC_NUM_FAILED_VCS =
            "Number of failed VCs before halting the proving process";
    private static final String FLAG_DESC_PROVER_TIMEOUT =
            "Number of Milliseconds to Use as a Timeout Before Skipping Proving a VC.";
    private static final String[] VCSFAILED_ARGS = { "vcsfailed" };
    private static final String[] FLAG_TIMEOUT_ARGS = { "milliseconds" };

    //flags
    /**
     * Tells the compiler to prove the sequent VCs
     */
    public static final Flag FLAG_PROVE =
            new Flag(FLAG_SECTION_NAME, "sprove", FLAG_DESC_GP_PROVER); //sequent prove
    private static final Flag FLAG_TIMEOUT =
            new Flag(FLAG_SECTION_NAME, "timeout", FLAG_DESC_PROVER_TIMEOUT);
    private static final Flag FLAG_VCSFAILED =
            new Flag(FLAG_SECTION_NAME, "vcsfailed", FLAG_DESC_NUM_FAILED_VCS,
                    VCSFAILED_ARGS, Flag.Type.HIDDEN);

    /**
     * <p>
     * Add all the required and implied flags for the
     * {@code GeneralPurposeProver}.
     * </p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PROVE, FLAG_VERIFY_VC); //adding prove flag, also add verify VC flag
        FlagDependencies.addRequires(FLAG_TIMEOUT, FLAG_PROVE);
        FlagDependencies.addRequires(FLAG_VCSFAILED, FLAG_PROVE);
    }

    /*
     * public GeneralPurposeProver(CompileEnvironment CompileEnvironment,
     * ModuleScope CurrentModuleScope) {
     * myCompileEnvironment = CompileEnvironment;
     * myCurrentModuleScope = CurrentModuleScope;
     * myOutputListeners = myCompileEnvironment.getOutputListeners();
     * myTypeGraph = myCompileEnvironment.getTypeGraph();
     * 
     * //proving process timeout
     * 
     * if (myCompileEnvironment.flags.isFlagSet(FLAG_TIMEOUT)) {
     * myTimeout = Long.parseLong(myCompileEnvironment.flags
     * .getFlagArgument(FLAG_TIMEOUT, "milliseconds"));
     * }
     * else {
     * myTimeout = 5000;
     * }
     * 
     * //number of failed VCs before halting
     * if (myCompileEnvironment.flags.isFlagSet(FLAG_VCSFAILED)) {
     * myNumVCsBeforeHalting = Integer.parseInt(myCompileEnvironment.flags
     * .getFlagArgument(FLAG_VCSFAILED, "vcsfailed"));
     * }
     * else {
     * myNumVCsBeforeHalting = -1;
     * }
     * 
     * }
     */

    public GeneralPurposeProver() {
        System.out.println("Test Prove ....");
    }

}
