/*
 * CircularDependencyException.java
 * ---------------------------------
 * Copyright (c) 2018
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
 * <p>An {@code CircularDependencyException} indicates an unresolvable
 * circular dependency between two (or more) modules.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 2.0
 */
public class CircularDependencyException extends CompilerException {

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
     * that caused a circular dependency exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    public CircularDependencyException(String message) {
        super(message, (Throwable) null);
    }

}