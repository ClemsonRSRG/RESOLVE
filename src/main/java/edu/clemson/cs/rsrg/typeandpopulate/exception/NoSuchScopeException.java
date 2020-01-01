/*
 * NoSuchScopeException.java
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
package edu.clemson.cs.rsrg.typeandpopulate.exception;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;

/**
 * <p>
 * A {@code NoSuchScopeException} indicates we encountered a scope that does not
 * exist in our symbol
 * table or in any of our scopes.
 * </p>
 *
 * @version 2.0
 */
public class NoSuchScopeException extends NoSuchSymbolException {

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
     * The element we attempted to request a scope.
     * </p>
     */
    public final ResolveConceptualElement requestedScope;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructor takes in a message and a throwable cause that resulted
     * in this exception.
     * </p>
     *
     * @param e The element that we cannot locate a scope for.
     */
    public NoSuchScopeException(ResolveConceptualElement e) {
        super("Cannot locate scope for: " + e, new RuntimeException());
        requestedScope = e;
    }

}
