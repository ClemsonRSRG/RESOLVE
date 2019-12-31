/*
 * BindingException.java
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
package edu.clemson.cs.rsrg.prover.exception;

import edu.clemson.cs.rsrg.statushandling.exception.CompilerException;

/**
 * <p>
 * A {@code BindingException} indicates we encountered an error while attempting
 * to bind an
 * expression.
 * </p>
 *
 * @version 2.0
 */
public class BindingException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Serial version for Serializable objects
     * </p>
     */
    private static final long serialVersionUID = -6126191138057900190L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructor creates a binding exception that will be handled by the
     * automated prover.
     * </p>
     */
    public BindingException() {
        super("", (Throwable) null);
    }

}
