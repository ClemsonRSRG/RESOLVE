/**
 * DuplicateEntryException.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.exception;

import edu.clemson.cs.rsrg.statushandling.exception.CompilerException;

/**
 * <p>A <code>DuplicateEntryException</code> indicates that the user-provided
 * entry already is a duplicate of another entry.</p>
 *
 * @author Hampton Smith
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class DuplicateEntryException extends CompilerException {

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
     * that caused a duplicate entry exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    public DuplicateEntryException(String message) {
        super(message, (Throwable) null);
    }

}