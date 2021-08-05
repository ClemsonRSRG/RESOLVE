/*
 * VCGenPipeline.java
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
package edu.clemson.rsrg.init.pipeline;

import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.output.OutputListener;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.vcgeneration.VCGenerator;

/**
 * <p>
 * This is pipeline that generates verification conditions (VCs) using the RESOLVE AST and symbol table.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class VCGenPipeline extends AbstractPipeline {

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
    public VCGenPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
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
        VCGenerator vcGenerator = new VCGenerator(mySymbolTable, myCompileEnvironment);
        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Generating VCs---------------\n\n");
            sb.append("Generating VCs for: ");
            sb.append(moduleDec.getName());

            statusHandler.info(null, sb.toString());
        }

        // Walk the AST and generate VCs
        TreeWalker.visit(vcGenerator, moduleDec);

        // Output the contents to listener objects
        for (OutputListener listener : myCompileEnvironment.getOutputListeners()) {
            listener.vcGeneratorResult(myCompileEnvironment.getFile(currentTarget).toString(),
                    moduleDec.getName().getName(), vcGenerator.getFinalAssertiveCodeBlocks(),
                    vcGenerator.getVerboseModeOutput());
        }

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------End Generating VCs---------------\n");

            statusHandler.info(null, sb.toString());
        }
    }

}
