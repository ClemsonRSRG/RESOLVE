/*
 * ImportException.java
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
package edu.clemson.cs.rsrg.statushandling.exception;

/**
 * <p>An {@code ImportException} indicates we encountered an error
 * while trying to import a new module.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class ImportException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a message
     * that caused an import exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    public ImportException(String message) {
        super(message, (Throwable) null);
    }

}