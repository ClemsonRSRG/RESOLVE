/*
 * ProverPipeline.java
 * ---------------------------------
 * Copyright (c) 2024
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
import edu.clemson.rsrg.prover.CongruenceClassProver;
import edu.clemson.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.ArrayList;

/**
 * <p>
 * This is pipeline that invokes an automated prover to verify verification conditions (VCs) using the symbol table.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ProverPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This generates a pipeline to prove VCs.
     * </p>
     *
     * @param ce
     *            The current compilation environment.
     * @param symbolTable
     *            The symbol table.
     */
    public ProverPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
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
        CongruenceClassProver prover = new CongruenceClassProver(new ArrayList<ImmutableVC>(),
                mySymbolTable.getModuleScope(currentTarget), myCompileEnvironment);
    }

}
