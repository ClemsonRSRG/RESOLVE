/*
 * ParameterModeChecker.java
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
package edu.clemson.rsrg.parametermodechecking;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ConceptModuleDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;

import java.util.List;

public class ParameterModeChecker extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The symbol table we are currently building.
     * </p>
     */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    private final CompileEnvironment myCompileEnvironment;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "ParameterModeChecker";
    private static final String FLAG_DESC_CHECK_PARAM_MODE = "Check Parameter Modes.";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * Tells the compiler to check parameter modes.
     * </p>
     */
    public static final Flag FLAG_CHECK_PARAMETER_MODES = new Flag(FLAG_SECTION_NAME, "PMC",
            FLAG_DESC_CHECK_PARAM_MODE);

    public static void setUpFlags() {

    }

    public ParameterModeChecker(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myBuilder = builder;
    }


    @Override
    public boolean walkOperationDec(OperationDec dec) {
        AssertionClause ensuresClause = dec.getEnsures();
        Exp assertionExp = ensuresClause.getAssertionExp();

        List<ParameterVarDec> parameters = dec.getParameters();
        for (ParameterVarDec parameter : parameters) {
            switch (parameter.getMode()) {
                case RESTORES:
                case PRESERVES:
                case EVALUATES:
                case REPLACES:
                    if (assertionExp.containsVar(parameter.getName().asString(0, 0), true)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Ensures clause at ");
                        sb.append(assertionExp.getLocation().toString());
                        sb.append(" contains #");
                        sb.append(parameter.getName().asString(0, 0));
                        sb.append(" while having parameter mode ");
                        sb.append(parameter.getMode().toString());
                        System.out.println(sb.toString());
                    }
                    break;
                case ALTERS:
                    if (assertionExp.containsVar(parameter.getName().asString(0, 0), false)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Ensures clause at ");
                        sb.append(assertionExp.getLocation().toString());
                        sb.append(" contains ");
                        sb.append(parameter.getName().asString(0, 0));
                        sb.append(" while having parameter mode ");
                        sb.append(parameter.getMode().toString());
                        System.out.println(sb.toString());
                    }
            }
        }

        return true;
    }
}
