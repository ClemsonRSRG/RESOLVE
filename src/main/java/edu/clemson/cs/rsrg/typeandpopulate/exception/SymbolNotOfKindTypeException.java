/*
 * SymbolNotOfKindTypeException.java
 * ---------------------------------
 * Copyright (c) 2019
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
 * <p>An {@code SymbolNotOfKindTypeException} indicates we encountered a
 * symbol that is not a type that produces a type value.</p>
 *
 * @version 2.0
 */
public class SymbolNotOfKindTypeException extends SymbolTableException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a message that caused this
     * exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    public SymbolNotOfKindTypeException(String message) {
        super(message, null);
    }

}