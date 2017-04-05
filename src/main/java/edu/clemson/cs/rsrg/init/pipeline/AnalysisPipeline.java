/**
 * AnalysisPipeline.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.Populator;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

public class AnalysisPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This generates a pipeline to populate new symbols and
     * perform semantic analysis.</p>
     *
     * @param ce The current compilation environment.
     * @param symbolTable The symbol table.
     */
    public AnalysisPipeline(CompileEnvironment ce,
            MathSymbolTableBuilder symbolTable) {
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
        Populator populator =
                new Populator(mySymbolTable, myCompileEnvironment);
        myCompileEnvironment.setTypeGraph(populator.getTypeGraph());
        TreeWalker.visit(populator, moduleDec);

        if (myCompileEnvironment.flags
                .isFlagSet(Populator.FLAG_POPULATOR_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Current Type Graph---------------\n\n");
            sb.append(mySymbolTable.getTypeGraph().toString());
            sb.append("\n---------------Current Type Graph---------------\n");
            statusHandler.info(null, sb.toString());
        }
    }

}