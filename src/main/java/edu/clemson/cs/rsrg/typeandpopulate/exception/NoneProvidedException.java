/*
 * NoneProvidedException.java
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
package edu.clemson.cs.rsrg.typeandpopulate.exception;

import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;

/**
 * <p>An {@code NoneProvidedException} indicates we encountered an
 * error where the user did not provide a required field.</p>
 *
 * @version 2.0
 */
public class NoneProvidedException extends SourceErrorException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a message for the location
     * that caused an source exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param location Location where the error occurred.
     */
    public NoneProvidedException(String message, Location location) {
        super(message, location);
    }

}