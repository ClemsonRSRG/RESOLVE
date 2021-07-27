/*
 * DuplicateSymbolException.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.exception;

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;

/**
 * <p>
 * A {@code DuplicateSymbolException} indicates we encountered a duplicate
 * symbol.
 * </p>
 *
 * @version 2.0
 */
public class DuplicateSymbolException extends SymbolTableException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Serial version for Serializable objects
     * </p>
     */
    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * The existing entry found in the symbol table.
     * </p>
     */
    private final SymbolTableEntry myExistingEntry;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructor takes in a message that caused this exception to be
     * thrown.
     * </p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param existing The existing symbol in the symbol table.
     */
    public DuplicateSymbolException(String message, SymbolTableEntry existing) {
        super(message, null);
        myExistingEntry = existing;
    }

}
