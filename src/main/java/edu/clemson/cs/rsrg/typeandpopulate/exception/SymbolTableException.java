/*
 * SymbolTableException.java
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

import edu.clemson.cs.rsrg.statushandling.exception.CompilerException;

/**
 * <p>An exception that inherits from {@code SymbolTableException} indicates we encountered
 * some sort of error when building the symbol table.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public abstract class SymbolTableException extends CompilerException {

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a throwable cause and a message
     * that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param cause   Cause of the exception.
     */
    protected SymbolTableException(String message, Throwable cause) {
        super(message, cause);
    }

}