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

import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;

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

    // ===========================================================
    // Constructors
    // ===========================================================

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