/*
 * CTranslator.java
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
package edu.clemson.cs.rsrg.translation.targets;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;

/**
 * <p>This class translates a {@code RESOLVE} source file
 * to {@code C}.</p>
 *
 * @author Daniel Welch
 * @author Mark Todd
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class CTranslator extends AbstractTranslator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Flag Strings
    // ===========================================================

    /** <p>Description for {@code cTranslate} flag.</p> */
    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE code to C.";

    /** <p>Description for {@code cTranslateClean} flag.</p> */
    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates C code for all supporting RESOLVE files.";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>The main translator flag. Tells the compiler convert
     * {@code RESOLVE} source code to {@code C} source code.</p>
     */
    public static final Flag C_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "cTranslate", FLAG_DESC_TRANSLATE);

    /**
     * <p>Tells the compiler to regenerate {@code C} code for all
     * supporting {@code RESOLVE} source files.</p>
     */
    public static final Flag C_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "cTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    /**
     * <p>Add all the required and implied flags for the {@code CTranslator}.</p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(C_FLAG_TRANSLATE, FLAG_TRANSLATE);
        FlagDependencies.addImplies(C_FLAG_TRANSLATE_CLEAN, FLAG_TRANSLATE);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that overrides methods to translate
     * a {@link ModuleDec} into a {@code C} source file.</p>
     *
     * @param builder            A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     */
    public CTranslator(MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment) {
        super(builder, compileEnvironment);
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Protected Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

}