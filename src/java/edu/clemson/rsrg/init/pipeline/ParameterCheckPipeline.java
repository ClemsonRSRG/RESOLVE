/*
 * ParameterCheckPipeline.java
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
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.parametermodechecking.ParameterModeChecker;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import jdk.jshell.Snippet;

public class ParameterCheckPipeline extends AbstractPipeline {
    /**
     * <p>
     * An helper constructor that allow us to store the {@link CompileEnvironment} and {@link MathSymbolTableBuilder}
     * from a class that inherits from {@code AbstractPipeline}.
     * </p>
     *
     * @param ce
     *            The current compilation environment.
     * @param symbolTable
     *            The symbol table.
     */
    public ParameterCheckPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
    }

    @Override
    public void process(ModuleIdentifier currentTarget) {
        ModuleDec moduleDec = myCompileEnvironment.getModuleAST(currentTarget);
        StatusHandler statusHandler = myCompileEnvironment.getStatusHandler();
        ParameterModeChecker parameterModeChecker = new ParameterModeChecker(mySymbolTable, myCompileEnvironment);

        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n-----------------Checking Parameter Modes-----------------\n\n");
            sb.append("Checking Parameter Modes for: ");
            sb.append(moduleDec.getName());

            statusHandler.info(null, sb.toString());
        }

        TreeWalker.visit(parameterModeChecker, moduleDec);
    }
}
