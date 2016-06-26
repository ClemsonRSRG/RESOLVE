/**
 * NullProgramTypeException.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.rsrg.statushandling.exception.CompilerException;

/**
 * <p>An {@code NullProgramTypeException} indicates we encountered an
 * null {@link PTType} and is trying to use it in some way.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class NullProgramTypeException extends CompilerException {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Serial version for Serializable objects</p> */
    private static final long serialVersionUID = 1L;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This constructor takes in a message that caused this
     * exception to be thrown.</p>
     *
     * @param message Message to be displayed when the exception is thrown.
     */
    public NullProgramTypeException(String message) {
        super(message, (Throwable) null);
    }

}