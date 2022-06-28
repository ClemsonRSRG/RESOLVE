/*
 * AnalysisPipeline.java
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
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.Populator;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>
 * This is pipeline that populates, sanity checks and type checks the RESOLVE AST.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class AnalysisPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This generates a pipeline to populate new symbols and perform semantic analysis.
     * </p>
     *
     * @param ce
     *            The current compilation environment.
     * @param symbolTable
     *            The symbol table.
     */
    public AnalysisPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
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
        Populator populator = new Populator(mySymbolTable, myCompileEnvironment);
        myCompileEnvironment.setTypeGraph(populator.getTypeGraph());
        TreeWalker.visit(populator, moduleDec);

        if (myCompileEnvironment.flags.isFlagSet(Populator.FLAG_POPULATOR_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Current Type Graph---------------\n\n");
            sb.append(mySymbolTable.getTypeGraph().toString());
            sb.append("\n---------------End Current Type Graph---------------\n");
            statusHandler.info(null, sb.toString());
        }
    }

}
