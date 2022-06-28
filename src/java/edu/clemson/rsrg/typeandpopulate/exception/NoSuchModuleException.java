/*
 * NoSuchModuleException.java
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

import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>
 * A {@code NoSuchModuleException} indicates we attempted to request a module that is not found in the source module.
 * </p>
 *
 * @version 2.0
 */
public class NoSuchModuleException extends SymbolTableException {

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
     * The source module's identifier.
     * </p>
     */
    public final ModuleIdentifier sourceModule;

    /**
     * <p>
     * The requested module's identifier.
     * </p>
     */
    public final ModuleIdentifier requestedModule;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructor takes the two {@link ModuleIdentifier} that caused this exception to be thrown.
     * </p>
     *
     * @param source
     *            The source module's identifier.
     * @param requested
     *            The requested module's identifier.
     */
    public NoSuchModuleException(ModuleIdentifier source, ModuleIdentifier requested) {
        super("Cannot locate the requested module: " + requested + " in the source module: " + source, null);
        sourceModule = source;
        requestedModule = requested;
    }

}
