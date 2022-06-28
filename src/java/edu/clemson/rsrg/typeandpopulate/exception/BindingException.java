/*
 * BindingException.java
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
package edu.clemson.rsrg.typeandpopulate.exception;

import edu.clemson.rsrg.statushandling.exception.CompilerException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;

/**
 * <p>
 * A {@code BindingException} indicates we encountered an error while attempting to bind an expression to a
 * {@link MTType}.
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
    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * The object we are attempting to bind.
     * </p>
     */
    public final Object found;

    /**
     * <p>
     * The object we expect to bind to.
     * </p>
     */
    public final Object expected;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructor takes both the expected object and the found object and forms the appropriate exception message.
     * </p>
     *
     * @param found
     *            The object we are attempting to bind.
     * @param expected
     *            The object we expect to bind to.
     */
    public BindingException(Object found, Object expected) {
        super("Expecting: " + expected + "\nFound: " + found, (Throwable) null);
        this.found = found;
        this.expected = expected;
    }

}
