/*
 * GraphicalASTOutputPipeline.java
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
import edu.clemson.rsrg.astoutput.GenerateGraphvizModel;
import edu.clemson.rsrg.init.output.OutputListener;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>
 * This is pipeline that generates graphical representations of a module AST.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class GraphicalASTOutputPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This generates a pipeline to generate a graphical representation of a module AST.
     * </p>
     *
     * @param ce
     *            The current compilation environment.
     * @param symbolTable
     *            The symbol table.
     */
    public GraphicalASTOutputPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
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
        ModuleDec dec = myCompileEnvironment.getModuleAST(currentTarget);
        StatusHandler statusHandler = myCompileEnvironment.getStatusHandler();
        STGroup group = new STGroupFile("templates/ASTOutput.stg");

        // Generate DOT File (GV extension)
        // Add all the nodes and edges
        String moduleName = dec.getName().getName();
        GenerateGraphvizModel twv = new GenerateGraphvizModel(group,
                group.getInstanceOf("outputGraphvizGVFile").add("moduleName", moduleName));
        TreeWalker.visit(twv, dec);

        // Output the contents to listener objects
        for (OutputListener listener : myCompileEnvironment.getOutputListeners()) {
            listener.astGraphvizModelResult(dec.getName().getName(), twv.getCompleteModel());
        }

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Output Module AST---------------\n\n");
            sb.append("Exported ModuleDec to dot file: ");
            sb.append(moduleName);
            sb.append("\n");
            sb.append("\n---------------End Output Module AST---------------\n");

            statusHandler.info(null, sb.toString());
        }
    }

}
