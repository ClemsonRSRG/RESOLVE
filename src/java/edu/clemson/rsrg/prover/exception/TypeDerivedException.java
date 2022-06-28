/*
 * TypeDerivedException.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.exception;

import edu.clemson.rsrg.statushandling.exception.CompilerException;

/**
 * <p>
 * A {@code BindingException} indicates we encountered an error while attempting to derive a mathematical type.
 * </p>
 *
 * @version 2.0
 */
public class TypeDerivedException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    public static final TypeDerivedException INSTANCE = new TypeDerivedException();

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
     * This constructor creates a type derived exception that will be handled by the automated prover.
     * </p>
     */
    private TypeDerivedException() {
        super("", (Throwable) null);
    }

}
