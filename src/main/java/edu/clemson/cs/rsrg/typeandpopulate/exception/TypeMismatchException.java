/*
 * TypeMismatchException.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.exception;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;

/**
 * <p>
 * An {@code TypeMismatchException} indicates we encountered two {@link MTType}s
 * or {@link PTType}
 * that are not compatible.
 * </p>
 *
 * @version 2.0
 */
public class TypeMismatchException extends SymbolTableException {

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
     * This constructor takes in a message that caused this exception to be
     * thrown.
     * </p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    public TypeMismatchException(String message) {
        super(message, null);
    }

}
