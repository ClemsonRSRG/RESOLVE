/*
 * AbstractTranslator.java
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
package edu.clemson.cs.rsrg.translation;

import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.Flag.Type;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;

/**
 * <p>This is the abstract base class for all target language translators
 * using the RESOLVE abstract syntax tree. This visitor logic is implemented as
 * a {@link TreeWalkerStackVisitor}.</p>
 *
 * @author Daniel Welch
 * @author Mark Todd
 * @author Yu-Shan Sun
 * @version 2.0
 */
public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    /**
     * <p>This indicates that this section translates {@code RESOLVE}
     * source files to other target languages.</p>
     */
    protected static final String FLAG_SECTION_NAME = "Translation";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>An auxiliary flag that indicates we are translating to
     * a target file.</p>
     */
    public static final Flag FLAG_TRANSLATE =
            new Flag(
                    FLAG_SECTION_NAME,
                    "translate",
                    "An auxiliary flag that indicates we are translating a source file",
                    Type.AUXILIARY);

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that creates and stores all the common
     * objects used by classes that inherit from this class.</p>
     *
     * @param builder A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    protected AbstractTranslator(MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment) {
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
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

}