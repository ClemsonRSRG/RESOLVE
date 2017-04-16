/*
 * VCGenPipeline.java
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
import edu.clemson.cs.rsrg.init.output.OutputListener;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import java.util.Date;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>This is pipeline that generates verification conditions (VCs)
 * using the RESOLVE AST and symbol table.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class VCGenPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This generates a pipeline to generate VCs.</p>
     *
     * @param ce The current compilation environment.
     * @param symbolTable The symbol table.
     */
    public VCGenPipeline(CompileEnvironment ce,
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
        // String template to hold the VC generation details
        STGroup group = new STGroupFile("templates/VCGenOutput.stg");
        String fileName =
                myCompileEnvironment.getFile(currentTarget).toString();
        ST model =
                group.getInstanceOf("outputVCGenFile")
                        .add("fileName", fileName).add("dateGenerated",
                                new Date());

        ModuleDec moduleDec = myCompileEnvironment.getModuleAST(currentTarget);
        StatusHandler statusHandler = myCompileEnvironment.getStatusHandler();
        VCGenerator vcGenerator =
                new VCGenerator(mySymbolTable, myCompileEnvironment, group,
                        model);
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
        for (OutputListener listener : myCompileEnvironment
                .getOutputListeners()) {
            listener.vcGeneratorResult(moduleDec, vcGenerator
                    .getFinalAssertiveCodeBlocks(), vcGenerator
                    .getCompleteModel());
        }

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------End Generating VCs---------------\n");

            statusHandler.info(null, sb.toString());
        }
    }

}