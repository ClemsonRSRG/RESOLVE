/**
 * SourceErrorException.java
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

import org.antlr.v4.runtime.Token;

/**
 * <p>The default source error exception for the compiler.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SourceErrorException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a throwable cause and a message for the
     * token that caused an source exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param t Offending token
     * @param cause Cause of the exception.
     */
    public SourceErrorException(String message, Token t, Throwable cause) {
        super(message, t, cause);
    }

    /**
     * <p>This constructor takes in a message for the
     * token that caused an source exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param t Offending token
     */
    public SourceErrorException(String message, Token t) {
        super(message, t);
    }

}