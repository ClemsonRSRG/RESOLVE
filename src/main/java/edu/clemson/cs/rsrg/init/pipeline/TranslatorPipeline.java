/*
 * TranslatorPipeline.java
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
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;
import edu.clemson.cs.rsrg.translation.targets.CTranslator;
import edu.clemson.cs.rsrg.translation.targets.JavaTranslator;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>This is pipeline that uses the RESOLVE AST and symbol table
 * and translates to the various supported target languages.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TranslatorPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This generates a pipeline to translate a file
     * to the various supported target languages.</p>
     *
     * @param ce The current compilation environment.
     * @param symbolTable The symbol table.
     */
    public TranslatorPipeline(CompileEnvironment ce,
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

        // Check to see if we are translating to Java
        boolean isJavaTranslateFlagOn =
                myCompileEnvironment.flags
                        .isFlagSet(JavaTranslator.JAVA_FLAG_TRANSLATE)
                        || myCompileEnvironment.flags
                                .isFlagSet(JavaTranslator.JAVA_FLAG_TRANSLATE_CLEAN);
        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------Begin Translation---------------\n\n");
            sb.append("Translating: ");
            sb.append(moduleDec.getName());
            sb.append(" to ");
            if (isJavaTranslateFlagOn) {
                sb.append("Java");
            }
            else {
                sb.append("C");
            }

            statusHandler.info(null, sb.toString());
        }

        // Create the appropriate translator
        AbstractTranslator translator;
        if (isJavaTranslateFlagOn) {
            translator =
                    new JavaTranslator(mySymbolTable, myCompileEnvironment);
        }
        else {
            translator = new CTranslator(mySymbolTable, myCompileEnvironment);
        }

        // Walk the AST and translate into the appropriate target source file
        TreeWalker.visit(translator, moduleDec);

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n---------------End Translation---------------\n");

            statusHandler.info(null, sb.toString());
        }
    }

}