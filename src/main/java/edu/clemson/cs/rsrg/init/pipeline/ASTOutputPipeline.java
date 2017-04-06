/*
 * ASTOutputPipeline.java
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
package edu.clemson.cs.rsrg.init.pipeline;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.astoutput.GenerateGraphvizModel;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import java.io.*;

/**
 * <p>This is pipeline that generates graphical representations
 * of the RESOLVE AST.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ASTOutputPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This generates a pipeline to generate files that aid the
     * debugging process.</p>
     *
     * @param ce The current compilation environment.
     * @param symbolTable The symbol table.
     */
    public ASTOutputPipeline(CompileEnvironment ce,
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
        ModuleDec dec = myCompileEnvironment.getModuleAST(currentTarget);
        STGroup group = new STGroupFile("templates/ASTOutput.stg");

        // Generate DOT File (GV extension)
        genModuleDecDotFile(dec, group);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This generates a .dot file representation.</p>
     *
     * @param moduleDec A {@link ModuleDec} node.
     * @param group A string template containing groups for
     *              generating the Graphviz model.
     */
    private void genModuleDecDotFile(ModuleDec moduleDec, STGroup group) {
        // Add all the nodes and edges
        String moduleName = moduleDec.getName().getName();
        GenerateGraphvizModel twv =
                new GenerateGraphvizModel(group, group.getInstanceOf(
                        "outputGraphvizGVFile").add("moduleName", moduleName));
        TreeWalker.visit(twv, moduleDec);

        // Write the contents to file
        String outputFileName = moduleName + "_ModuleDec.gv";
        writeToFile(outputFileName, twv.getCompleteModel());

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Output Module AST---------------\n\n");
            sb.append("Exported ModuleDec to dot file: ");
            sb.append(outputFileName);
            sb.append("\n");
            sb.append("\n---------------End Output Module AST---------------\n");

            myCompileEnvironment.getStatusHandler().info(null, sb.toString());
        }
    }

    /**
     * <p>Writes the content to the specified filename.</p>
     *
     * @param outputFileName Output filename.
     * @param outputString Contents to be written in file.
     */
    private void writeToFile(String outputFileName, String outputString) {
        try {
            // Write the contents to file
            Writer writer =
                    new BufferedWriter(new FileWriter(new File(outputFileName),
                            false));
            writer.write(outputString);
            writer.close();
        }
        catch (IOException ioe) {
            myCompileEnvironment.getStatusHandler().error(null,
                    "Error while writing to file: " + outputFileName);
        }
    }

}