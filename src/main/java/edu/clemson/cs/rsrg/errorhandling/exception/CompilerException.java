/**
 * CompilerException.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.errorhandling.exception;

import edu.clemson.cs.rsrg.parsing.data.ResolveToken;

/**
 * <p>The abstract parent class for all runtime exceptions the compiler.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public abstract class CompilerException extends RuntimeException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Token that caused the exception to be thrown</p> */
    private final ResolveToken myOffendingToken;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a throwable cause and a message for the
     * token that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param t Offending token
     * @param cause Cause of the exception.
     */
    protected CompilerException(String message, ResolveToken t, Throwable cause) {
        super(message, cause);
        myOffendingToken = t;
    }

    /**
     * <p>This constructor takes in a message for the
     * token that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param t Offending token
     */
    protected CompilerException(String message, ResolveToken t) {
        this(message, t, null);
    }

    /**
     * <p>This constructor takes in a throwable cause and a message
     * that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param cause Cause of the exception.
     */
    protected CompilerException(String message, Throwable cause) {
        this(message, null, cause);
    }

    /**
     * <p>This constructor takes in a message
     * that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    protected CompilerException(String message) {
        this(message, null, null);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Return the token that caused this exception to be thrown.</p>
     *
     * @return A token object.
     */
    public ResolveToken getOffendingToken() {
        return myOffendingToken;
    }

}