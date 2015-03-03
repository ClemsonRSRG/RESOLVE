/**
 * VCGenerator.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.vcgeneration2;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;

/**
 * TODO: Write a description of this module
 */
public class VCGenerator {

    // ===========================================================
    // Global Variables
    // ===========================================================

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "GenerateVCs";
    private static final String FLAG_DESC_VERIFY_VC = "Generate VCs.";
    /*private static final String FLAG_DESC_PERF_VC =
            "Generate Performance VCs";*/

    // ===========================================================
    // Flags
    // ===========================================================

    public static final Flag FLAG_VERIFY_VC =
            new Flag(FLAG_SECTION_NAME, "VCs", FLAG_DESC_VERIFY_VC);

    /*public static final Flag FLAG_PVCS_VC =
            new Flag(FLAG_SECTION_NAME, "PVCs", FLAG_DESC_PERF_VC);

    public static final void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PVCS_VC, FLAG_VERIFY_VC);
    }*/

    // ===========================================================
    // Constructors
    // ===========================================================

    public VCGenerator(ScopeRepository table, final CompileEnvironment env) {

    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Prover Mode
    // -----------------------------------------------------------

    // ===========================================================
    // Private Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Proof Rules
    // -----------------------------------------------------------
}
