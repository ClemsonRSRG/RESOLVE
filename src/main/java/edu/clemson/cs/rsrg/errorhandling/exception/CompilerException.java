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

import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>The abstract parent class for all runtime exceptions the compiler.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public abstract class CompilerException extends RuntimeException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Location that caused the exception to be thrown</p> */
    private final Location myErrorLocation;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a throwable cause and a message
     * that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param location Location where the exception originated from.
     */
    protected CompilerException(String message, Location location) {
        this(message, location, null);
    }

    /**
     * <p>This constructor takes in a message for the
     * symbol that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param symbol Offending symbol
     */
    protected CompilerException(String message, PosSymbol symbol) {
        this(message, symbol, null);
    }

    /**
     * <p>This constructor takes in a throwable cause and a message
     * that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param cause Cause of the exception.
     */
    protected CompilerException(String message, Throwable cause) {
        this(message, (Location) null, cause);
    }

    /**
     * <p>This constructor takes in a throwable cause and a message for the
     * symbol that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param symbol Offending symbol
     * @param cause Cause of the exception.
     */
    protected CompilerException(String message, PosSymbol symbol,
            Throwable cause) {
        this(message, symbol.getLocation(), cause);
    }

    /**
     * <p>This constructor takes in a throwable cause, a message and a location
     * that caused the exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     * @param location Location where the exception originated from.
     * @param cause Cause of the exception.
     */
    protected CompilerException(String message, Location location,
            Throwable cause) {
        super(message, cause);
        myErrorLocation = location;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Return the location that caused this exception to be thrown.</p>
     *
     * @return A {link Location} object.
     */
    public Location getErrorLocation() {
        return myErrorLocation;
    }

}