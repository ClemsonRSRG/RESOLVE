/*
 * VCGenerator.java
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
package edu.clemson.cs.rsrg.vcgeneration;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class generates verification conditions (VCs) using the provided
 * RESOLVE abstract syntax tree. This visitor logic is implemented as
 * a {@link TreeWalkerVisitor}.</p>
 *
 * @author Heather Keown Harton
 * @author Yu-Shan Sun
 * @version 3.0
 */
public class VCGenerator extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /** <p>String template for the VC generation model.</p> */
    private final ST myModel;

    /** <p>String template for the VC generation details model.</p> */
    private final ST myVCGenDetailsModel;

    /** <p>String template groups for storing all the VC generation details.</p> */
    private final STGroup mySTGroup;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "VCGenerator";
    private static final String FLAG_DESC_VERIFY_VC = "Generate VCs.";
    private static final String FLAG_DESC_PERF_VC = "Generate Performance VCs";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>Tells the compiler to generate VCs.</p>
     */
    public static final Flag FLAG_VERIFY_VC =
            new Flag(FLAG_SECTION_NAME, "VCs", FLAG_DESC_VERIFY_VC);

    /**
     * <p>Tells the compiler to generate performance VCs.</p>
     */
    private static final Flag FLAG_PVCS_VC =
            new Flag(FLAG_SECTION_NAME, "PVCs", FLAG_DESC_PERF_VC);

    /**
     * <p>Add all the required and implied flags for the {@code VCGenerator}.</p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PVCS_VC, FLAG_VERIFY_VC);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that overrides methods to generate VCs from
     * a {@link ModuleDec}.</p>
     *
     * @param builder A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     * @param stGroup The string template file.
     * @param model The model we are going be generating.
     */
    public VCGenerator(MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment, STGroup stGroup, ST model) {
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myModel = model;
        mySTGroup = stGroup;
        myVCGenDetailsModel = mySTGroup.getInstanceOf("outputVCGenDetails");
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Enhancement Realization Module
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting an {@link EnhancementRealizModuleDec}.</p>
     *
     * @param enhancementRealization An enhancement realization module declaration.
     */
    @Override
    public final void preEnhancementRealizModuleDec(
            EnhancementRealizModuleDec enhancementRealization) {
        // Concept Module Identifier
        PosSymbol conceptName = enhancementRealization.getConceptName();
        ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());

        // Enhancement Module Identifier
        PosSymbol enhancementName = enhancementRealization.getEnhancementName();
        ModuleIdentifier enId = new ModuleIdentifier(enhancementName.getName());

        // Add to VC detail model
        ST header =
                mySTGroup.getInstanceOf("outputEnhancementRealizHeader").add(
                        "realizName",
                        enhancementRealization.getName().getName()).add(
                        "enhancementName", enhancementName.getName()).add(
                        "conceptName", conceptName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the completed model with all the {@code VCs}
     * and the generation details.</p>
     *
     * @return String template rendering of the model.
     */
    public final String getCompleteModel() {
        // TODO: Add the VC output
        // Add the VC generation details to the model
        myModel.add("details", myVCGenDetailsModel.render());

        return myModel.render();
    }

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