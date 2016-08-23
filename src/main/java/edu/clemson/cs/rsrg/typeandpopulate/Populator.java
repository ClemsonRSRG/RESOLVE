/**
 * Populator.java
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
package edu.clemson.cs.rsrg.typeandpopulate;

import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;

/**
 * <p>This class populates the symbol table and assigns mathematical types to the
 * provided RESOLVE abstract syntax tree. This visitor logic is implemented as a
 * a {@link TreeWalkerVisitor}.</p>
 *
 * @version 2.0
 */
public class Populator extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ModuleDec
    // -----------------------------------------------------------

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method prints debugging messages if that flag is
     * enabled by the user.</p>
     *
     * @param msg Message to be displayed.
     */
    public static void emitDebug(String msg) {

    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Helper Constructs
    // ===========================================================

}