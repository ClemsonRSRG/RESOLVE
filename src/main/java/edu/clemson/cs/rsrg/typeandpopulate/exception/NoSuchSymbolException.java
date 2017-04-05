/*
 * NoSuchSymbolException.java
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
package edu.clemson.cs.rsrg.typeandpopulate.exception;

/**
 * <p>A {@code NoSuchSymbolException} indicates we encountered a
 * symbol that does not exist in our symbol table or in any of our scopes.</p>
 *
 * @version 2.0
 */
public class NoSuchSymbolException extends SymbolTableException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a message and a throwable cause
     * that resulted in this exception.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param cause Cause of the exception.
     */
    public NoSuchSymbolException(String message, Throwable cause) {
        super(message, cause);
    }

}