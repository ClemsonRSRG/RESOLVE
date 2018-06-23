/*
 * CTranslator.java
 * ---------------------------------
 * Copyright (c) 2018
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
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

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
        // Always need to set the auxiliary flag
        FlagDependencies.addImplies(C_FLAG_TRANSLATE, FLAG_TRANSLATE);
        FlagDependencies.addImplies(C_FLAG_TRANSLATE_CLEAN, FLAG_TRANSLATE);

        // Translate clean requires the regular translate flag
        FlagDependencies.addRequires(C_FLAG_TRANSLATE_CLEAN, C_FLAG_TRANSLATE);

        // Setup debugging for java translator
        FlagDependencies.addImplies(FLAG_TRANSLATE_DEBUG,
                ResolveCompiler.FLAG_DEBUG);
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
        super(builder, compileEnvironment, new STGroupFile("templates/C.stg"));
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

    /**
     * <p>This method returns the default function modifier specified
     * by the target language.</p>
     *
     * @return Function modifier as a string.
     */
    @Override
    protected final String getFunctionModifier() {
        return null;
    }

    /**
     * <p>This method returns the operation type template for
     * the target language.</p>
     *
     * @param type A {@link PTType}.
     *
     * @return A {@link ST} associated with the given
     * program type.
     */
    @Override
    protected final ST getOperationTypeTemplate(PTType type) {
        return null;
    }

    /**
     * <p>This method returns the program parameter type template for
     * the target language.</p>
     *
     * @param type A {@link PTType}.
     *
     * @return A {@link ST} associated with the given
     * program parameter type.
     */
    @Override
    protected final ST getParameterTypeTemplate(PTType type) {
        return null;
    }

    /**
     * <p>This method returns the program variable type template for
     * the target language.</p>
     *
     * @param type A {@link PTType}.
     *
     * @return A {@link ST} associated with the given
     * program variable type.
     */
    protected final ST getVariableTypeTemplate(PTType type) {
        return null;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

}