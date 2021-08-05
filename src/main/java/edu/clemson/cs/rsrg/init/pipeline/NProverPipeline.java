/*
 * NProverPipeline.java
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
package edu.clemson.cs.rsrg.init.pipeline;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.cs.rsrg.prover.CongruenceClassProver;
import edu.clemson.cs.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

import java.util.ArrayList;

/**
 * <p>
 * This is pipeline that invokes an automated nprover to verify sequent
 * verification
 * conditions (VCs) using the symbol table.
 * </p>
 */
public class NProverPipeline extends AbstractPipeline {

    public NProverPipeline(CompileEnvironment ce,
            MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
    }

    //public methods
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
        GeneralPurposeProver prover = new GeneralPurposeProver();
    }
}
