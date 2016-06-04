/**
 * DebugOutputPipeline.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.astoutput.GenerateGraphvizModel;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.ModuleIdentifier;
import java.io.*;

public class DebugOutputPipeline extends AbstractPipeline {

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
    public DebugOutputPipeline(CompileEnvironment ce,
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
        // Walk the module
        ModuleDec dec = myCompileEnvironment.getModuleAST(currentTarget);
        GenerateGraphvizModel twv = new GenerateGraphvizModel();
        TreeWalker tw = new TreeWalker(twv);
        tw.visit(dec);

        // Nodes and Strings
        String nodes = twv.getNodeList().toString();
        String arrows = twv.getArrowList().toString();

        // Generate SVG File
        genModuleDecSVGFile(dec.getName().getName() + "_ModuleDec.svg", nodes,
                arrows);

        // Generate DOT File (GV extension)
        genModuleDecDotFile(dec.getName().getName() + "_ModuleDec.gv", nodes,
                arrows);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This generates a .dot file representation.</p>
     *
     * @param outputFileName The output file name.
     * @param nodes The different nodes in the module as a string.
     * @param arrows The arrows connecting the nodes as a string.
     */
    private void genModuleDecDotFile(String outputFileName, String nodes,
            String arrows) {
        StringBuffer sb = new StringBuffer();

        // Header
        sb.append("digraph {\n\n");
        sb.append("\tordering=out;\n");
        sb.append("\tranksep=.4;\n");
        sb
                .append("\tbgcolor=\"lightgrey\"; node [shape=box, fixedsize=false, fontsize=12, fontname=\"Helvetica-bold\", fontcolor=\"blue\"\n");
        sb
                .append("\t\twidth=.25, height=.25, color=\"black\", fillcolor=\"white\", style=\"filled, solid, bold\"];\n");
        sb.append("\tedge [arrowsize=.5, color=\"black\", style=\"bold\"]\n");
        sb.append("\n");
        sb.append(nodes);
        sb.append(arrows);
        sb.append("\n");
        sb.append("}\n");

        writeToFile(outputFileName, sb.toString());

        myCompileEnvironment.getErrorHandler().info(null,
                "Exported ModuleDec to dot file: " + outputFileName);
    }

    /**
     * <p>This generates a .svg file representation.</p>
     *
     * @param outputFileName The output file name.
     * @param nodes The different nodes in the module as a string.
     * @param arrows The arrows connecting the nodes as a string.
     */
    private void genModuleDecSVGFile(String outputFileName, String nodes,
            String arrows) {
        StringBuffer sb = new StringBuffer();

        // Header
        sb.append("<?xml version=\"1.0\" standalone=\"no\"?>\n\n");
        sb
                .append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        sb.append("\tordering=out;\n");
        sb.append("\tranksep=.4;\n");
        sb
                .append("\tbgcolor=\"lightgrey\"; node [shape=box, fixedsize=false, fontsize=12, fontname=\"Helvetica-bold\", fontcolor=\"blue\"\n");
        sb
                .append("\t\twidth=.25, height=.25, color=\"black\", fillcolor=\"white\", style=\"filled, solid, bold\"];\n");
        sb.append("\tedge [arrowsize=.5, color=\"black\", style=\"bold\"]\n");
        sb.append("\n");
        sb.append(nodes);
        sb.append(arrows);
        sb.append("\n");
        sb.append("}\n");

        writeToFile(outputFileName, sb.toString());

        myCompileEnvironment.getErrorHandler().info(null,
                "Exported ModuleDec to svg file: " + outputFileName);
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
            myCompileEnvironment.getErrorHandler().error(null,
                    "Error while writing to file: " + outputFileName);
        }
    }

}