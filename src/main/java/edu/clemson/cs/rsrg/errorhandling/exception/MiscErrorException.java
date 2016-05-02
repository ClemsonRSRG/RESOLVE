/**
 * MiscErrorException.java
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
package edu.clemson.cs.rsrg.errorhandling.exception;

/**
 *  <p>The miscellaneous error exception for the compiler
 *  where there is no line or location information available.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class MiscErrorException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 2L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a throwable cause and a message for the
     * that caused an source exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param cause Cause of the exception.
     */
    public MiscErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}