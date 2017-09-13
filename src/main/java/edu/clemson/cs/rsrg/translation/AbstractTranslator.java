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

import edu.clemson.cs.rsrg.treewalk.TreeWalkerStackVisitor;

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

}