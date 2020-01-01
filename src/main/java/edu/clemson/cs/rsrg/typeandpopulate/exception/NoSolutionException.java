/*
 * NoSolutionException.java
 * ---------------------------------
 * Copyright (c) 2020
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
 * <p>
 * An {@code NoSolutionException} indicates we encountered an error in our type
 * reasoning process
 * where we don't know how to recover from.
 * </p>
 *
 * @version 2.0
 */
public class NoSolutionException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Serial version for Serializable objects
     * </p>
     */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructor takes in a message and a throwable cause that resulted
     * in this exception.
     * </p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param cause Cause of the exception.
     */
    public NoSolutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
