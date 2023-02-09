/*
 * NotPSymbolException.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.exception;

import edu.clemson.rsrg.prover.absyn.PExp;
import edu.clemson.rsrg.prover.absyn.expressions.PSymbol;
import edu.clemson.rsrg.statushandling.exception.CompilerException;

/**
 * <p>
 * A {@code NotPSymbolException} indicates we encountered an expression that isn't of type {@link PSymbol}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class NotPSymbolException extends CompilerException {

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
     * This constructor creates an exception that indicates the automated prover encountered a non-{@link PSymbol}
     * expression.
     * </p>
     */
    public NotPSymbolException(PExp exp) {
        super("unhandled PExp: " + exp.toString(), (Throwable) null);
    }

}
