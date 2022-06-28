/*
 * NProverPipeline.java
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
package edu.clemson.rsrg.init.pipeline;

import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.List;

/**
 * <p>
 * This is pipeline that invokes an automated nprover to verify sequent verification conditions (VCs) using the symbol
 * table.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Nicodemus Msafiri J. M.
 *
 * @version 1.0
 */
public class NProverPipeline extends AbstractPipeline {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The module's final list of verification conditions.
     * </p>
     */
    private final List<VerificationCondition> myVerificationConditions;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This generates a pipeline to generate VCs.
     * </p>
     *
     * @param ce
     *            The current compilation environment.
     * @param symbolTable
     *            The symbol table.
     */
    public NProverPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable, List<VerificationCondition> vcs) {
        super(ce, symbolTable);
        myVerificationConditions = vcs;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final void process(ModuleIdentifier currentTarget) {
        ModuleDec moduleDec = myCompileEnvironment.getModuleAST(currentTarget);
        StatusHandler statusHandler = myCompileEnvironment.getStatusHandler();
        GeneralPurposeProver prover = new GeneralPurposeProver(myVerificationConditions,
                mySymbolTable.getModuleScope(currentTarget), myCompileEnvironment);
        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Proving VCs---------------\n\n");
            sb.append("Proving VCs in: ");
            sb.append(moduleDec.getName());

            statusHandler.info(null, sb.toString());
        }

        // Invoke the prover on all vcs.
        prover.proveVCs();

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------End Proving VCs---------------\n");

            statusHandler.info(null, sb.toString());
        }
    }
}
